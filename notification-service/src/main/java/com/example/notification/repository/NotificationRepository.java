package com.example.notification.repository;

import com.example.notification.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import com.example.notification.model.EmailDeliveryStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByRecipientOrderByTimestampDesc(String recipient);
    List<Notification> findByRecipientAndInAppVisibleTrueOrderByTimestampDesc(String recipient);
    Page<Notification> findByRecipientAndInAppVisibleTrue(String recipient,Pageable pageable);
    long countByRecipientAndIsReadFalse(String recipient);
    long countByRecipientAndIsReadFalseAndInAppVisibleTrue(String recipient);
    Optional<Notification> findByIdAndRecipient(Long id, String recipient);
    List<Notification> findByEmailStatusAndEmailAttemptsLessThanOrderByTimestampAsc(EmailDeliveryStatus status,int attempts,Pageable pageable);
    List<Notification> findByRecipientAndEmailStatusOrderByTimestampAsc(String recipient,EmailDeliveryStatus status);
}
