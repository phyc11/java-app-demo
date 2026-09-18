package com.example.auth.service;
import com.example.auth.model.*; import com.example.auth.repository.*; import org.springframework.beans.factory.annotation.Value; import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional;
import java.nio.charset.StandardCharsets; import java.security.MessageDigest; import java.time.LocalDateTime; import java.util.*;
import com.example.auth.dto.AuthSessionDTO; import java.util.stream.Collectors;
@Service
public class TokenService {
 private final RefreshTokenRepository refresh; private final UserActionTokenRepository actions; private final long refreshDays;
 public TokenService(RefreshTokenRepository r,UserActionTokenRepository a,@Value("${jwt.refresh-expiration-days:30}") long days){refresh=r;actions=a;refreshDays=days;}
 @Transactional public String issueRefresh(Long userId){String raw=random();refresh.save(new RefreshToken(userId,hash(raw),LocalDateTime.now().plusDays(refreshDays)));return raw;}
 @Transactional public Rotation rotate(String raw){RefreshToken old=findRefresh(raw);if(old.isRevoked()||old.getExpiresAt().isBefore(LocalDateTime.now()))throw new IllegalArgumentException("Refresh token is invalid or expired");String next=random();old.setRevoked(true);old.setReplacedByHash(hash(next));refresh.save(old);refresh.save(new RefreshToken(old.getUserId(),hash(next),LocalDateTime.now().plusDays(refreshDays)));return new Rotation(old.getUserId(),next);}
 @Transactional public void revoke(String raw){RefreshToken t=findRefresh(raw);t.setRevoked(true);refresh.save(t);}
 @Transactional public void revokeAll(Long uid){List<RefreshToken> list=refresh.findByUserIdAndRevokedFalse(uid);list.forEach(t->t.setRevoked(true));refresh.saveAll(list);}
 public List<AuthSessionDTO> sessions(Long uid){return refresh.findByUserIdOrderByCreatedAtDesc(uid).stream().map(AuthSessionDTO::new).collect(Collectors.toList());}
 @Transactional public void revokeSession(Long uid,Long sessionId){if(sessionId==null)throw new IllegalArgumentException("Session ID is required");RefreshToken token=refresh.findByIdAndUserId(sessionId,uid).orElseThrow(()->new IllegalArgumentException("Session not found"));if(!token.isRevoked()){token.setRevoked(true);refresh.save(token);}}
 @Transactional public String action(Long uid,UserActionToken.Type type,long minutes){String raw=random();actions.save(new UserActionToken(uid,hash(raw),type,LocalDateTime.now().plusMinutes(minutes)));return raw;}
 @Transactional public Long consume(String raw,UserActionToken.Type type){UserActionToken t=actions.findByTokenHashAndType(hash(raw),type).orElseThrow(()->new IllegalArgumentException("Token is invalid"));if(t.getUsedAt()!=null||t.getExpiresAt().isBefore(LocalDateTime.now()))throw new IllegalArgumentException("Token is invalid or expired");t.setUsedAt(LocalDateTime.now());actions.save(t);return t.getUserId();}
 private RefreshToken findRefresh(String raw){return refresh.findByTokenHash(hash(raw)).orElseThrow(()->new IllegalArgumentException("Refresh token is invalid"));}
 private String random(){byte[] b=new byte[32];new java.security.SecureRandom().nextBytes(b);return Base64.getUrlEncoder().withoutPadding().encodeToString(b);}
 private String hash(String s){if(s==null)throw new IllegalArgumentException("Token is required");try{byte[] bytes=MessageDigest.getInstance("SHA-256").digest(s.getBytes(StandardCharsets.UTF_8));StringBuilder out=new StringBuilder();for(byte b:bytes)out.append(String.format("%02x",b));return out.toString();}catch(Exception e){throw new IllegalStateException(e);}}
 public static class Rotation{public final Long userId;public final String token;Rotation(Long u,String t){userId=u;token=t;}}
}
