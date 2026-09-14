package com.example.auth.dto;

import com.example.auth.model.Role;
import com.example.auth.model.User;

public class UserDTO {
    private Long id;
    private String username;
    private String fullName;
    private String avatarColor;
    private Role role;

    public UserDTO() {}

    public UserDTO(User user) {
        if (user != null) {
            this.id = user.getId();
            this.username = user.getUsername();
            this.fullName = user.getFullName();
            this.avatarColor = user.getAvatarColor() != null ? user.getAvatarColor() : "#6366f1";
            this.role = user.getRole();
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
}
