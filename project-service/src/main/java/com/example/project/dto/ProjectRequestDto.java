package com.example.project.dto;

public class ProjectRequestDto {
    private String name;
    private String projectKey;
    private String description;
    private String ownerUsername;

    public ProjectRequestDto() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getProjectKey() { return projectKey; }
    public void setProjectKey(String projectKey) { this.projectKey = projectKey; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getOwnerUsername() { return ownerUsername; }
    public void setOwnerUsername(String ownerUsername) { this.ownerUsername = ownerUsername; }
}
