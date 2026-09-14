package com.example.project.repository;

import com.example.project.model.ProjectMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long> {
    List<ProjectMember> findByProjectId(Long projectId);
    List<ProjectMember> findByUsername(String username);
    Optional<ProjectMember> findByProjectIdAndUsername(Long projectId, String username);
    void deleteByProjectIdAndUsername(Long projectId, String username);
}
