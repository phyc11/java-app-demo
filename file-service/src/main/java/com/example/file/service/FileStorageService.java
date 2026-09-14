package com.example.file.service;

import com.example.file.dto.FileUploadResponseDto;
import com.example.file.model.FileMetadata;
import com.example.file.repository.FileMetadataRepository;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FileStorageService {
    private final FileMetadataRepository repository;
    private final S3Client s3;
    private final S3Presigner presigner;
    private final VirusScanner virusScanner;
    private final FileAccessService access;
    private final Tika tika = new Tika();
    private final String bucket;
    private final long maxSize;
    private final int expirationMinutes;
    private final Set<String> allowedTypes;
    private final int cleanupAgeDays;

    public FileStorageService(FileMetadataRepository repository, S3Client s3, S3Presigner presigner,
                              VirusScanner virusScanner, FileAccessService access,
                              @Value("${file.storage.bucket:taskcraft-files}") String bucket,
                              @Value("${file.storage.max-size-bytes:10485760}") long maxSize,
                              @Value("${file.storage.presigned-url-expiration-minutes:15}") int expirationMinutes,
                              @Value("${file.storage.allowed-types}") String allowedTypes,
                              @Value("${file.cleanup.age-days:1}") int cleanupAgeDays) {
        this.repository=repository; this.s3=s3; this.presigner=presigner; this.virusScanner=virusScanner;
        this.access=access; this.bucket=bucket; this.maxSize=maxSize; this.expirationMinutes=expirationMinutes;
        this.allowedTypes=Arrays.stream(allowedTypes.split(",")).map(String::trim).map(String::toLowerCase).collect(Collectors.toSet());
        this.cleanupAgeDays=cleanupAgeDays;
    }

    @PostConstruct
    void createBucket() {
        try { s3.headBucket(HeadBucketRequest.builder().bucket(bucket).build()); }
        catch (S3Exception e) { s3.createBucket(CreateBucketRequest.builder().bucket(bucket).build()); }
    }

    @Transactional
    public FileUploadResponseDto storeFile(MultipartFile file, String entityType, Long entityId,
                                           Long workspaceId, String uploadedBy) {
        requireIdentity(workspaceId, uploadedBy);
        if (file.isEmpty()) throw new IllegalArgumentException("Cannot upload an empty file");
        if (file.getSize() > maxSize) throw new IllegalArgumentException("File exceeds maximum size of " + maxSize + " bytes");
        String type = entityType == null ? "TASK" : entityType.trim().toUpperCase();
        if ("TASK".equals(type)) access.assertTaskInWorkspace(entityId, workspaceId);
        byte[] bytes;
        try { bytes=file.getBytes(); } catch (IOException e) { throw new IllegalArgumentException("Cannot read upload", e); }
        String original=StringUtils.cleanPath(Optional.ofNullable(file.getOriginalFilename()).orElse("unnamed"));
        if (original.contains("..")) throw new IllegalArgumentException("Invalid filename");
        String detected;
        try { detected=tika.detect(bytes, original).toLowerCase(); } catch (Exception e) { throw new IllegalArgumentException("Cannot detect MIME type",e); }
        if (!allowedTypes.contains(detected)) throw new IllegalArgumentException("Detected MIME type is not allowed: " + detected);
        virusScanner.assertClean(bytes);
        String key=workspaceId + "/" + UUID.randomUUID();
        s3.putObject(PutObjectRequest.builder().bucket(bucket).key(key).contentType(detected).contentLength((long)bytes.length).build(), RequestBody.fromBytes(bytes));
        try {
            FileMetadata metadata=repository.save(new FileMetadata(original,key,detected,(long)bytes.length,"S3",type,entityId,workspaceId,uploadedBy));
            return mapToDto(metadata);
        } catch (RuntimeException e) { deleteObject(key); throw e; }
    }

    public FileMetadata getFileMetadata(Long fileId, Long workspaceId) {
        FileMetadata metadata=repository.findById(fileId).orElseThrow(() -> new IllegalArgumentException("File not found: " + fileId));
        assertWorkspace(metadata, workspaceId); return metadata;
    }

    public List<FileUploadResponseDto> getFilesByEntity(String entityType, Long entityId, Long workspaceId) {
        return repository.findByEntityTypeAndEntityId(entityType.toUpperCase(),entityId).stream()
                .filter(f -> workspaceId.equals(f.getWorkspaceId())).map(this::mapToDto).collect(Collectors.toList());
    }

    @Transactional
    public void deleteFile(Long fileId, Long workspaceId, String username, String workspaceRole) {
        FileMetadata metadata=getFileMetadata(fileId,workspaceId);
        if (!metadata.getUploadedBy().equalsIgnoreCase(username) && !"OWNER".equals(workspaceRole) && !"ADMIN".equals(workspaceRole))
            throw new SecurityException("Only uploader, workspace owner or admin can delete this file");
        deleteObject(metadata.getStoredFileName()); repository.delete(metadata);
    }

    public FileUploadResponseDto mapToDto(FileMetadata metadata) {
        GetObjectRequest get=GetObjectRequest.builder().bucket(bucket).key(metadata.getStoredFileName())
                .responseContentDisposition("attachment; filename=\"" + metadata.getOriginalFileName().replace("\"","") + "\"").build();
        String url=presigner.presignGetObject(GetObjectPresignRequest.builder().signatureDuration(Duration.ofMinutes(expirationMinutes)).getObjectRequest(get).build()).url().toString();
        FileUploadResponseDto dto=new FileUploadResponseDto(); dto.setId(metadata.getId()); dto.setOriginalFileName(metadata.getOriginalFileName());
        dto.setContentType(metadata.getContentType()); dto.setFileSize(metadata.getFileSize()); dto.setEntityType(metadata.getEntityType());
        dto.setEntityId(metadata.getEntityId()); dto.setWorkspaceId(metadata.getWorkspaceId()); dto.setUploadedBy(metadata.getUploadedBy());
        dto.setUploadedAt(metadata.getUploadedAt()); dto.setPresignedDownloadUrl(url); dto.setExpirationMinutes(expirationMinutes); return dto;
    }

    @Scheduled(cron="${file.cleanup.cron:0 0 3 * * *}")
    @Transactional
    public void cleanupOrphans() {
        repository.findByUploadedAtBefore(LocalDateTime.now().minusDays(cleanupAgeDays)).stream()
                .filter(f -> "TASK".equals(f.getEntityType()) && !access.taskExists(f.getEntityId(),f.getWorkspaceId()))
                .forEach(f -> { deleteObject(f.getStoredFileName()); repository.delete(f); });
    }

    private void requireIdentity(Long workspaceId,String user){if(workspaceId==null)throw new IllegalArgumentException("X-Workspace-Id is required");if(!StringUtils.hasText(user))throw new SecurityException("Trusted X-User is required");}
    private void assertWorkspace(FileMetadata f,Long id){if(id==null||!id.equals(f.getWorkspaceId()))throw new SecurityException("File does not belong to this workspace");}
    private void deleteObject(String key){s3.deleteObject(DeleteObjectRequest.builder().bucket(bucket).key(key).build());}
}
