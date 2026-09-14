package com.example.workspace.repository;

import com.example.workspace.model.Workspace;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WorkspaceRepository extends JpaRepository<Workspace, Long> {
    Optional<Workspace> findBySlug(String slug);
    List<Workspace> findByIdIn(List<Long> ids);
}
