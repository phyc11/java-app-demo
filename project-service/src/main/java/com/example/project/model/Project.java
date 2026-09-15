package com.example.project.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "projects", uniqueConstraints=@UniqueConstraint(name="uk_project_workspace_key",columnNames={"workspaceId","projectKey"}))
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Version private Long version;
    @Column(nullable=false) private Long workspaceId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, length = 20)
    private String projectKey;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private String ownerUsername;

    private String status; // ACTIVE, ARCHIVED

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Project() {
        this.status = "ACTIVE";
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Project(Long workspaceId, String name, String projectKey, String description, String ownerUsername) {
        this.workspaceId = workspaceId;
        this.name = name;
        this.projectKey = projectKey != null ? projectKey.toUpperCase() : null;
        this.description = description;
        this.ownerUsername = ownerUsername;
        this.status = "ACTIVE";
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getVersion(){return version;} public void setVersion(Long v){version=v;}
    public Long getWorkspaceId(){return workspaceId;} public void setWorkspaceId(Long v){workspaceId=v;}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getProjectKey() { return projectKey; }
    public void setProjectKey(String projectKey) { this.projectKey = projectKey != null ? projectKey.toUpperCase() : null; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getOwnerUsername() { return ownerUsername; }
    public void setOwnerUsername(String ownerUsername) { this.ownerUsername = ownerUsername; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
