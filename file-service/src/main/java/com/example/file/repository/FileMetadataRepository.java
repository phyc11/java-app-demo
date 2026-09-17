package com.example.file.repository;

import com.example.file.model.FileMetadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface FileMetadataRepository extends JpaRepository<FileMetadata, Long> {
    List<FileMetadata> findByEntityTypeAndEntityId(String entityType, Long entityId);
    Page<FileMetadata> findByEntityTypeAndEntityIdAndWorkspaceId(String entityType,Long entityId,Long workspaceId,Pageable pageable);
    List<FileMetadata> findByUploadedAtBefore(LocalDateTime cutoff);
}
