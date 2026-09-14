package com.example.auth.repository;
import com.example.auth.model.UserActionToken; import org.springframework.data.jpa.repository.JpaRepository; import java.util.Optional;
public interface UserActionTokenRepository extends JpaRepository<UserActionToken,Long>{Optional<UserActionToken> findByTokenHashAndType(String hash,UserActionToken.Type type);}
