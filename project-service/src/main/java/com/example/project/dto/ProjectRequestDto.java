package com.example.project.dto;

public class ProjectRequestDto {
    private String name;
    private String projectKey;
    private String description;
    private String ownerUsername;
    private Long workspaceId;
    private Long version;
    private Long templateId;

    public ProjectRequestDto() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getProjectKey() { return projectKey; }
    public void setProjectKey(String projectKey) { this.projectKey = projectKey; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getOwnerUsername() { return ownerUsername; }
    public void setOwnerUsername(String ownerUsername) { this.ownerUsername = ownerUsername; }
    public Long getWorkspaceId(){return workspaceId;} public void setWorkspaceId(Long v){workspaceId=v;}
    public Long getVersion(){return version;} public void setVersion(Long v){version=v;}
    public Long getTemplateId(){return templateId;} public void setTemplateId(Long v){templateId=v;}
}
