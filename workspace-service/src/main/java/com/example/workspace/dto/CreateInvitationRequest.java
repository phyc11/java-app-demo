package com.example.workspace.dto;

public class CreateInvitationRequest {
    private String email;
    private String role;
    private String invitedBy;
    private Integer expiresInHours;

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getInvitedBy() { return invitedBy; }
    public void setInvitedBy(String invitedBy) { this.invitedBy = invitedBy; }
    public Integer getExpiresInHours() { return expiresInHours; }
    public void setExpiresInHours(Integer expiresInHours) { this.expiresInHours = expiresInHours; }
}
