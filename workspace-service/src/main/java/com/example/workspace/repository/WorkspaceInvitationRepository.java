package com.example.workspace.repository;

import com.example.workspace.model.InvitationStatus;
import com.example.workspace.model.WorkspaceInvitation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WorkspaceInvitationRepository extends JpaRepository<WorkspaceInvitation, Long> {
    List<WorkspaceInvitation> findByWorkspaceIdOrderByCreatedAtDesc(Long workspaceId);
    Optional<WorkspaceInvitation> findByToken(String token);
    Optional<WorkspaceInvitation> findByWorkspaceIdAndEmailAndStatus(
            Long workspaceId, String email, InvitationStatus status);
}
