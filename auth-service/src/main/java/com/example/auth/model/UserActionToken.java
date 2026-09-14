package com.example.auth.model;
import javax.persistence.*; import java.time.LocalDateTime;
@Entity @Table(name="user_action_tokens",indexes=@Index(name="idx_action_hash",columnList="tokenHash",unique=true))
public class UserActionToken {
 public enum Type { EMAIL_VERIFICATION, PASSWORD_RESET }
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id; @Column(nullable=false) private Long userId;
 @Column(nullable=false,unique=true,length=64) private String tokenHash; @Enumerated(EnumType.STRING) private Type type;
 @Column(nullable=false) private LocalDateTime expiresAt; private LocalDateTime usedAt;
 public UserActionToken(){} public UserActionToken(Long uid,String hash,Type type,LocalDateTime expiry){userId=uid;tokenHash=hash;this.type=type;expiresAt=expiry;}
 public Long getUserId(){return userId;} public String getTokenHash(){return tokenHash;} public Type getType(){return type;}
 public LocalDateTime getExpiresAt(){return expiresAt;} public LocalDateTime getUsedAt(){return usedAt;} public void setUsedAt(LocalDateTime v){usedAt=v;}
}
