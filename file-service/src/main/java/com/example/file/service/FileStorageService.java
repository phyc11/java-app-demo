package com.example.file.service;

import com.example.file.dto.FileUploadResponseDto;
import com.example.file.model.FileMetadata;
import com.example.file.repository.FileMetadataRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class FileStorageService {

    @Value("${file.storage.local-dir:./uploads}")
    private String uploadDir;

    @Value("${file.storage.storage-type:LOCAL}")
    private String storageType;

    @Value("${file.storage.presigned-url-expiration-minutes:30}")
    private int presignedUrlExpirationMinutes;

    @Value("${file.storage.allowed-types:image/jpeg,image/png,image/gif,application/pdf,application/zip,text/plain}")
    private String allowedTypes;

    private final FileMetadataRepository fileMetadataRepository;
    private Path rootStoragePath;
    private final String secretKey = "TaskCraftSecretKeyForPresignedUrl";

    public FileStorageService(FileMetadataRepository fileMetadataRepository) {
        this.fileMetadataRepository = fileMetadataRepository;
    }

    @PostConstruct
    public void init() {
        try {
            this.rootStoragePath = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(this.rootStoragePath);
        } catch (Exception ex) {
            throw new RuntimeException("Could not initialize file storage directory!", ex);
        }
    }

    public FileUploadResponseDto storeFile(MultipartFile file, String entityType, Long entityId, String uploadedBy) {
        // 1. Validate File Empty & Content Type
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Cannot upload an empty file!");
        }

        String contentType = file.getContentType();
        List<String> allowedList = Arrays.asList(allowedTypes.split(","));
        if (contentType != null && !allowedList.contains(contentType.toLowerCase())) {
            throw new IllegalArgumentException("File type '" + contentType + "' is not supported. Allowed types: " + allowedTypes);
        }

        // 2. Generate unique filename
        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename() != null ? file.getOriginalFilename() : "unnamed");
        String extension = "";
        int extIndex = originalFileName.lastIndexOf(".");
        if (extIndex > 0) {
            extension = originalFileName.substring(extIndex);
        }
        String storedFileName = UUID.randomUUID().toString() + extension;

        // 3. Save file to Disk (or S3/MinIO provider)
        try {
            Path targetLocation = this.rootStoragePath.resolve(storedFileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            throw new RuntimeException("Could not store file " + originalFileName + ". Please try again!", ex);
        }

        // 4. Save Metadata to DB
        FileMetadata metadata = new FileMetadata(
                originalFileName,
                storedFileName,
                contentType,
                file.getSize(),
                storageType,
                entityType != null ? entityType.toUpperCase() : "TASK",
                entityId,
                uploadedBy
        );
        metadata = fileMetadataRepository.save(metadata);

        return mapToDto(metadata);
    }

    public Resource loadFileAsResource(Long fileId, String token, long expires) {
        FileMetadata metadata = fileMetadataRepository.findById(fileId)
                .orElseThrow(() -> new IllegalArgumentException("File attachment not found with ID: " + fileId));

        // Validate Presigned Token
        validatePresignedToken(fileId, token, expires);

        try {
            Path filePath = this.rootStoragePath.resolve(metadata.getStoredFileName()).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("File not found on storage disk: " + metadata.getOriginalFileName());
            }
        } catch (MalformedURLException ex) {
            throw new RuntimeException("File path is invalid!", ex);
        }
    }

    public FileMetadata getFileMetadata(Long fileId) {
        return fileMetadataRepository.findById(fileId)
                .orElseThrow(() -> new IllegalArgumentException("File attachment not found with ID: " + fileId));
    }

    public List<FileUploadResponseDto> getFilesByEntity(String entityType, Long entityId) {
        return fileMetadataRepository.findByEntityTypeAndEntityId(entityType.toUpperCase(), entityId)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public void deleteFile(Long fileId) {
        FileMetadata metadata = getFileMetadata(fileId);
        try {
            Path filePath = this.rootStoragePath.resolve(metadata.getStoredFileName());
            Files.deleteIfExists(filePath);
        } catch (IOException ignored) {}
        fileMetadataRepository.delete(metadata);
    }

    public FileUploadResponseDto mapToDto(FileMetadata metadata) {
        long expiresTimestamp = Instant.now().getEpochSecond() + (presignedUrlExpirationMinutes * 60L);
        String token = generatePresignedToken(metadata.getId(), expiresTimestamp);

        String presignedUrl = "/api/files/download/" + metadata.getId() + "?token=" + token + "&expires=" + expiresTimestamp;

        FileUploadResponseDto dto = new FileUploadResponseDto();
        dto.setId(metadata.getId());
        dto.setOriginalFileName(metadata.getOriginalFileName());
        dto.setContentType(metadata.getContentType());
        dto.setFileSize(metadata.getFileSize());
        dto.setEntityType(metadata.getEntityType());
        dto.setEntityId(metadata.getEntityId());
        dto.setUploadedBy(metadata.getUploadedBy());
        dto.setUploadedAt(metadata.getUploadedAt());
        dto.setPresignedDownloadUrl(presignedUrl);
        dto.setExpirationMinutes(presignedUrlExpirationMinutes);
        return dto;
    }

    private String generatePresignedToken(Long fileId, long expiresTimestamp) {
        String raw = fileId + ":" + expiresTimestamp + ":" + secretKey;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(raw.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            return String.valueOf(raw.hashCode());
        }
    }

    private void validatePresignedToken(Long fileId, String token, long expiresTimestamp) {
        if (token == null || expiresTimestamp <= 0) {
            throw new IllegalArgumentException("Access denied: missing presigned download token!");
        }

        if (Instant.now().getEpochSecond() > expiresTimestamp) {
            throw new IllegalArgumentException("Access denied: presigned URL has expired!");
        }

        String expectedToken = generatePresignedToken(fileId, expiresTimestamp);
        if (!expectedToken.equals(token)) {
            throw new IllegalArgumentException("Access denied: invalid presigned URL signature!");
        }
    }
}
