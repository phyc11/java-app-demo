package com.example.file.controller;

import com.example.common.dto.ApiResponse;
import com.example.file.dto.FileUploadResponseDto;
import com.example.file.model.FileMetadata;
import com.example.file.service.FileStorageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import org.springframework.data.domain.Page;

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
            @RequestHeader("X-Workspace-Id") Long workspaceId,
            @RequestHeader("X-User") String uploadedBy) {

        FileUploadResponseDto response = fileStorageService.storeFile(file, entityType, entityId, workspaceId, uploadedBy);
        return ResponseEntity.ok(ApiResponse.ok("File uploaded successfully", response));
    }

    @GetMapping("/{fileId}")
    public ResponseEntity<ApiResponse<FileUploadResponseDto>> getFileMetadata(@PathVariable Long fileId,
            @RequestHeader("X-Workspace-Id") Long workspaceId,@RequestHeader("X-User") String username) {
        FileMetadata metadata = fileStorageService.getFileMetadata(fileId, workspaceId,username);
        FileUploadResponseDto dto = fileStorageService.mapToDto(metadata);
        return ResponseEntity.ok(ApiResponse.ok("File metadata retrieved", dto));
    }

    @GetMapping("/entity/{entityType}/{entityId}")
    public ResponseEntity<ApiResponse<List<FileUploadResponseDto>>> getFilesByEntity(
            @PathVariable String entityType,
            @PathVariable Long entityId,
            @RequestHeader("X-Workspace-Id") Long workspaceId,@RequestHeader("X-User") String username,
            @RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="20") int size) {

        Page<FileUploadResponseDto> files = fileStorageService.getFilesByEntity(entityType,entityId,workspaceId,username,page,size);
        return ResponseEntity.ok(ApiResponse.okPage("Attachments retrieved for " + entityType + " #" + entityId,
                files.getContent(),files.getNumber(),files.getSize(),files.getTotalElements(),files.getTotalPages()));
    }

    @DeleteMapping("/{fileId}")
    public ResponseEntity<ApiResponse<Void>> deleteFile(@PathVariable Long fileId,
            @RequestHeader("X-Workspace-Id") Long workspaceId,
            @RequestHeader("X-User") String username,
            @RequestHeader(value="X-Workspace-Role", required=false) String workspaceRole) {
        fileStorageService.deleteFile(fileId, workspaceId, username, workspaceRole);
        return ResponseEntity.ok(ApiResponse.ok("Attachment deleted successfully", null));
    }
}
