package com.example.auth.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;
    @Column(nullable=false,unique=true,length=254) private String email;

    @Column(nullable = false)
    private String password;

    private String fullName;

    private String avatarColor;
    @Column(nullable=false) private boolean emailVerified=false;
    @Column(nullable=false) private boolean active=true;
    @Version private Long version;
    @Column(nullable=false,updatable=false) private LocalDateTime createdAt;
    @Column(nullable=false) private LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    public User() {}

    @PrePersist void onCreate(){createdAt=LocalDateTime.now();updatedAt=createdAt;}
    @PreUpdate void onUpdate(){updatedAt=LocalDateTime.now();}

    public User(String username, String password, String fullName, Role role) {
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.role = role;
        this.avatarColor = "#6366f1";
    }

    public User(String username, String password, String fullName, Role role, String avatarColor) {
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.role = role;
        this.avatarColor = avatarColor;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail(){return email;} public void setEmail(String value){email=value;}

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getAvatarColor() { return avatarColor; }
    public void setAvatarColor(String avatarColor) { this.avatarColor = avatarColor; }
    public boolean isEmailVerified(){return emailVerified;} public void setEmailVerified(boolean value){emailVerified=value;}
    public boolean isActive(){return active;} public void setActive(boolean value){active=value;}
    public Long getVersion(){return version;} public LocalDateTime getCreatedAt(){return createdAt;} public LocalDateTime getUpdatedAt(){return updatedAt;}

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
}
