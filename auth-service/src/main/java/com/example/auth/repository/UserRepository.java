package com.example.auth.repository;

import com.example.auth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.example.auth.model.Role;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Boolean existsByUsername(String username);
    Optional<User> findByUsernameIgnoreCase(String username);
    Boolean existsByEmailIgnoreCase(String email);
    long countByRoleAndActiveTrue(Role role);
    @Query("select u from User u where (:search is null or lower(u.username) like lower(concat('%',:search,'%')) or lower(u.email) like lower(concat('%',:search,'%')) or lower(u.fullName) like lower(concat('%',:search,'%'))) and (:active is null or u.active=:active) and (:role is null or u.role=:role)")
    Page<User> search(@Param("search")String search,@Param("active")Boolean active,@Param("role")Role role,Pageable pageable);
}
