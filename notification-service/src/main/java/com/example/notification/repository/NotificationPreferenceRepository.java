package com.example.notification.repository;

import com.example.notification.model.NotificationPreference;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NotificationPreferenceRepository extends JpaRepository<NotificationPreference, Long> {
    Optional<NotificationPreference> findByUsername(String username);
}
