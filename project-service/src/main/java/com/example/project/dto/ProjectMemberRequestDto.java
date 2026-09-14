package com.example.project.dto;

public class ProjectMemberRequestDto {
    private String username;
    private String role; // OWNER, PROJECT_LEAD, MEMBER, VIEWER

    public ProjectMemberRequestDto() {}

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
