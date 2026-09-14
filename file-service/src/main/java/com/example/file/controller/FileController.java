package com.example.file.controller;

import com.example.common.dto.ApiResponse;
import com.example.file.dto.FileUploadResponseDto;
import com.example.file.model.FileMetadata;
import com.example.file.service.FileStorageService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/files")
public class FileController {

    private final FileStorageService fileStorageService;

    public FileController(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<FileUploadResponseDto>> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "entityType", defaultValue = "TASK") String entityType,
            @RequestParam(value = "entityId", required = false) Long entityId,
            @RequestParam(value = "uploadedBy", defaultValue = "system") String uploadedBy) {

        FileUploadResponseDto response = fileStorageService.storeFile(file, entityType, entityId, uploadedBy);
        return ResponseEntity.ok(ApiResponse.ok("File uploaded successfully", response));
    }

    @GetMapping("/{fileId}")
    public ResponseEntity<ApiResponse<FileUploadResponseDto>> getFileMetadata(@PathVariable Long fileId) {
        FileMetadata metadata = fileStorageService.getFileMetadata(fileId);
        FileUploadResponseDto dto = fileStorageService.mapToDto(metadata);
        return ResponseEntity.ok(ApiResponse.ok("File metadata retrieved", dto));
    }

    @GetMapping("/download/{fileId}")
    public ResponseEntity<Resource> downloadFile(
            @PathVariable Long fileId,
            @RequestParam("token") String token,
            @RequestParam("expires") long expires) {

        Resource resource = fileStorageService.loadFileAsResource(fileId, token, expires);
        FileMetadata metadata = fileStorageService.getFileMetadata(fileId);

        String contentType = metadata.getContentType() != null ? metadata.getContentType() : "application/octet-stream";

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + metadata.getOriginalFileName() + "\"")
                .body(resource);
    }

    @GetMapping("/entity/{entityType}/{entityId}")
    public ResponseEntity<ApiResponse<List<FileUploadResponseDto>>> getFilesByEntity(
            @PathVariable String entityType,
            @PathVariable Long entityId) {

        List<FileUploadResponseDto> files = fileStorageService.getFilesByEntity(entityType, entityId);
        return ResponseEntity.ok(ApiResponse.ok("Found " + files.size() + " attachments for " + entityType + " #" + entityId, files));
    }

    @DeleteMapping("/{fileId}")
    public ResponseEntity<ApiResponse<Void>> deleteFile(@PathVariable Long fileId) {
        fileStorageService.deleteFile(fileId);
        return ResponseEntity.ok(ApiResponse.ok("Attachment deleted successfully", null));
    }
}
