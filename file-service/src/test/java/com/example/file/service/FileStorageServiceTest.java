package com.example.file.service;

import com.example.file.model.FileMetadata;
import com.example.file.repository.FileMetadataRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class FileStorageServiceTest {
    private FileMetadataRepository repository;
    private FileAccessService access;
    private FileStorageService service;

    @BeforeEach void setup() {
        repository=mock(FileMetadataRepository.class); access=mock(FileAccessService.class);
        service=new FileStorageService(repository,mock(S3Client.class),mock(S3Presigner.class),
                mock(VirusScanner.class),access,"files",5,15,"text/plain",1);
    }

    @Test void rejectsOversizedFileBeforeStorage() {
        MockMultipartFile file=new MockMultipartFile("file","a.txt","text/plain","123456".getBytes());
        assertThrows(IllegalArgumentException.class,()->service.storeFile(file,"TASK",1L,2L,"alice"));
    }

    @Test void rejectsFileFromAnotherWorkspace() {
        FileMetadata metadata=new FileMetadata("a.txt","key","text/plain",1L,"S3","TASK",1L,2L,"alice");
        when(repository.findById(3L)).thenReturn(Optional.of(metadata));
        assertThrows(SecurityException.class,()->service.getFileMetadata(3L,99L));
    }

    @Test void nonUploaderCannotDeleteWithoutAdminRole() {
        FileMetadata metadata=new FileMetadata("a.txt","key","text/plain",1L,"S3","TASK",1L,2L,"alice");
        when(repository.findById(3L)).thenReturn(Optional.of(metadata));
        assertThrows(SecurityException.class,()->service.deleteFile(3L,2L,"bob","MEMBER"));
        verify(repository,never()).delete(any());
    }
}
