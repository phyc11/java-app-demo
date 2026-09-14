package com.example.workspace.repository;

import com.example.workspace.model.WorkspaceMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WorkspaceMemberRepository extends JpaRepository<WorkspaceMember, Long> {
    List<WorkspaceMember> findByWorkspaceIdOrderByJoinedAtAsc(Long workspaceId);
    List<WorkspaceMember> findByUsername(String username);
    Optional<WorkspaceMember> findByWorkspaceIdAndUsername(Long workspaceId, String username);
    boolean existsByWorkspaceIdAndUsername(Long workspaceId, String username);
}
