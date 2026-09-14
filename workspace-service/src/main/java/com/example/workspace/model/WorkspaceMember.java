package com.example.workspace.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "workspace_members", uniqueConstraints =
        @UniqueConstraint(name = "uk_workspace_member", columnNames = {"workspaceId", "username"}))
public class WorkspaceMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long workspaceId;

    @Column(nullable = false, length = 100)
    private String username;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private WorkspaceRole role;

    @Column(nullable = false, updatable = false)
    private LocalDateTime joinedAt;

    public WorkspaceMember() {}

    public WorkspaceMember(Long workspaceId, String username, WorkspaceRole role) {
        this.workspaceId = workspaceId;
        this.username = username;
        this.role = role;
        this.joinedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getWorkspaceId() { return workspaceId; }
    public void setWorkspaceId(Long workspaceId) { this.workspaceId = workspaceId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public WorkspaceRole getRole() { return role; }
    public void setRole(WorkspaceRole role) { this.role = role; }
    public LocalDateTime getJoinedAt() { return joinedAt; }
    public void setJoinedAt(LocalDateTime joinedAt) { this.joinedAt = joinedAt; }
}
