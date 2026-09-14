package com.example.notification.repository;

import com.example.notification.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByRecipientOrderByTimestampDesc(String recipient);
    long countByRecipientAndIsReadFalse(String recipient);
    Optional<Notification> findByIdAndRecipient(Long id, String recipient);
}
