package com.example.project.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "project_members", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"projectId", "username"})
})
public class ProjectMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long projectId;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String role; // OWNER, PROJECT_LEAD, MEMBER, VIEWER

    private LocalDateTime joinedAt;

    public ProjectMember() {
        this.joinedAt = LocalDateTime.now();
    }

    public ProjectMember(Long projectId, String username, String role) {
        this.projectId = projectId;
        this.username = username;
        this.role = role != null ? role.toUpperCase() : "MEMBER";
        this.joinedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role != null ? role.toUpperCase() : "MEMBER"; }

    public LocalDateTime getJoinedAt() { return joinedAt; }
    public void setJoinedAt(LocalDateTime joinedAt) { this.joinedAt = joinedAt; }
}
