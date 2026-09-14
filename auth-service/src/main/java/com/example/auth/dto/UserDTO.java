package com.example.auth.dto;

import com.example.auth.model.Role;
import com.example.auth.model.User;

public class UserDTO {
    private Long id;
    private String username;
    private String fullName;
    private String avatarColor;
    private Role role;
    private String email; private boolean emailVerified; private boolean active; private Long version;
    private java.time.LocalDateTime createdAt,updatedAt;

    public UserDTO() {}

    public UserDTO(User user) {
        if (user != null) {
            this.id = user.getId();
            this.username = user.getUsername();
            this.fullName = user.getFullName();
            this.avatarColor = user.getAvatarColor() != null ? user.getAvatarColor() : "#6366f1";
            this.role = user.getRole();
            this.email=user.getEmail(); this.emailVerified=user.isEmailVerified(); this.active=user.isActive(); this.version=user.getVersion(); this.createdAt=user.getCreatedAt(); this.updatedAt=user.getUpdatedAt();
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getAvatarColor() { return avatarColor; }
    public void setAvatarColor(String avatarColor) { this.avatarColor = avatarColor; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
    public String getEmail(){return email;} public void setEmail(String v){email=v;} public boolean isEmailVerified(){return emailVerified;} public void setEmailVerified(boolean v){emailVerified=v;}
    public boolean isActive(){return active;} public void setActive(boolean v){active=v;} public Long getVersion(){return version;} public void setVersion(Long v){version=v;}
    public java.time.LocalDateTime getCreatedAt(){return createdAt;} public void setCreatedAt(java.time.LocalDateTime v){createdAt=v;} public java.time.LocalDateTime getUpdatedAt(){return updatedAt;} public void setUpdatedAt(java.time.LocalDateTime v){updatedAt=v;}
}
