package com.example.workspace.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "workspace_invitations", indexes = {
        @Index(name = "idx_invitation_token", columnList = "token", unique = true),
        @Index(name = "idx_invitation_workspace", columnList = "workspaceId")
})
public class WorkspaceInvitation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long workspaceId;

    @Column(nullable = false, length = 254)
    private String email;

    @Column(nullable = false, length = 100)
    private String invitedBy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private WorkspaceRole role;

    @Column(nullable = false, unique = true, length = 36)
    private String token;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private InvitationStatus status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    private LocalDateTime acceptedAt;

    @Column(length = 100)
    private String acceptedBy;

    public WorkspaceInvitation() {}

    public WorkspaceInvitation(Long workspaceId, String email, String invitedBy, WorkspaceRole role,
                               String token, LocalDateTime expiresAt) {
        this.workspaceId = workspaceId;
        this.email = email;
        this.invitedBy = invitedBy;
        this.role = role;
        this.token = token;
        this.status = InvitationStatus.PENDING;
        this.createdAt = LocalDateTime.now();
        this.expiresAt = expiresAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getWorkspaceId() { return workspaceId; }
    public void setWorkspaceId(Long workspaceId) { this.workspaceId = workspaceId; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getInvitedBy() { return invitedBy; }
    public void setInvitedBy(String invitedBy) { this.invitedBy = invitedBy; }
    public WorkspaceRole getRole() { return role; }
    public void setRole(WorkspaceRole role) { this.role = role; }
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public InvitationStatus getStatus() { return status; }
    public void setStatus(InvitationStatus status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
    public LocalDateTime getAcceptedAt() { return acceptedAt; }
    public void setAcceptedAt(LocalDateTime acceptedAt) { this.acceptedAt = acceptedAt; }
    public String getAcceptedBy() { return acceptedBy; }
    public void setAcceptedBy(String acceptedBy) { this.acceptedBy = acceptedBy; }
}
