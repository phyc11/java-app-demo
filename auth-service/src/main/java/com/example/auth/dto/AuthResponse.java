package com.example.auth.dto;

import com.example.auth.model.Role;

public class AuthResponse {
    private String token;
    private String username;
    private String fullName;
    private Role role;
    private String refreshToken;
    private long expiresIn;

    public AuthResponse() {}

    public AuthResponse(String token, String username, String fullName, Role role) {
        this.token = token;
        this.username = username;
        this.fullName = fullName;
        this.role = role;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
    public String getRefreshToken(){return refreshToken;} public void setRefreshToken(String value){refreshToken=value;}
    public long getExpiresIn(){return expiresIn;} public void setExpiresIn(long value){expiresIn=value;}
}
