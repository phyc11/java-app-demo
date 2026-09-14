package com.example.auth.repository;
import com.example.auth.model.RefreshToken; import org.springframework.data.jpa.repository.*; import org.springframework.data.repository.query.Param; import java.util.*;
public interface RefreshTokenRepository extends JpaRepository<RefreshToken,Long>{Optional<RefreshToken> findByTokenHash(String hash);List<RefreshToken> findByUserIdAndRevokedFalse(Long userId);}
