package com.example.auth.model;
import javax.persistence.*; import java.time.LocalDateTime;
@Entity @Table(name="refresh_tokens",indexes=@Index(name="idx_refresh_hash",columnList="tokenHash",unique=true))
public class RefreshToken {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false) private Long userId; @Column(nullable=false,unique=true,length=64) private String tokenHash;
 @Column(nullable=false) private LocalDateTime expiresAt; @Column(nullable=false) private boolean revoked=false;
 private LocalDateTime createdAt; private String replacedByHash;
 public RefreshToken(){} public RefreshToken(Long userId,String hash,LocalDateTime expiry){this.userId=userId;tokenHash=hash;expiresAt=expiry;createdAt=LocalDateTime.now();}
 public Long getId(){return id;} public Long getUserId(){return userId;} public String getTokenHash(){return tokenHash;}
 public LocalDateTime getExpiresAt(){return expiresAt;} public boolean isRevoked(){return revoked;} public void setRevoked(boolean v){revoked=v;}
 public String getReplacedByHash(){return replacedByHash;} public void setReplacedByHash(String v){replacedByHash=v;}
}
