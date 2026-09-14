package com.example.file.repository;

import com.example.file.model.FileMetadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.time.LocalDateTime;

@Repository
public interface FileMetadataRepository extends JpaRepository<FileMetadata, Long> {
    List<FileMetadata> findByEntityTypeAndEntityId(String entityType, Long entityId);
    List<FileMetadata> findByUploadedAtBefore(LocalDateTime cutoff);
}
