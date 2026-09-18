package com.example.auth.dto;

import com.example.auth.model.RefreshToken;
import java.time.LocalDateTime;

public class AuthSessionDTO {
    private Long id;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private LocalDateTime revokedAt;
    private boolean active;

    public AuthSessionDTO() {}
    public AuthSessionDTO(RefreshToken token) {
        id=token.getId();createdAt=token.getCreatedAt();expiresAt=token.getExpiresAt();revokedAt=token.getRevokedAt();
        active=!token.isRevoked()&&token.getExpiresAt().isAfter(LocalDateTime.now());
    }
    public Long getId(){return id;} public void setId(Long value){id=value;}
    public LocalDateTime getCreatedAt(){return createdAt;} public void setCreatedAt(LocalDateTime value){createdAt=value;}
    public LocalDateTime getExpiresAt(){return expiresAt;} public void setExpiresAt(LocalDateTime value){expiresAt=value;}
    public LocalDateTime getRevokedAt(){return revokedAt;} public void setRevokedAt(LocalDateTime value){revokedAt=value;}
    public boolean isActive(){return active;} public void setActive(boolean value){active=value;}
}
