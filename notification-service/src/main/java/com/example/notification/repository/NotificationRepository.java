package com.example.notification.repository;

import com.example.notification.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Collection;
import com.example.notification.model.EmailDeliveryStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import com.example.notification.model.NotificationType;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByRecipientOrderByTimestampDesc(String recipient);
    List<Notification> findByRecipientAndInAppVisibleTrueOrderByTimestampDesc(String recipient);
    Page<Notification> findByRecipientAndInAppVisibleTrue(String recipient,Pageable pageable);
    @Query("select n from Notification n where n.recipient=:recipient and n.inAppVisible=true " +
            "and (:type is null or n.type=:type) and (:unreadOnly=false or n.isRead=false)")
    Page<Notification> findInbox(@Param("recipient") String recipient,@Param("type") NotificationType type,
                                 @Param("unreadOnly") boolean unreadOnly,Pageable pageable);
    long countByRecipientAndIsReadFalse(String recipient);
    long countByRecipientAndIsReadFalseAndInAppVisibleTrue(String recipient);
    long countByRecipientAndInAppVisibleTrue(String recipient);
    List<Notification> findAllByIdInAndRecipientAndInAppVisibleTrue(Collection<Long> ids,String recipient);
    @Query("select n.type,count(n) from Notification n where n.recipient=:recipient and n.inAppVisible=true and n.isRead=false group by n.type")
    List<Object[]> countUnreadByType(@Param("recipient") String recipient);
    Optional<Notification> findByIdAndRecipient(Long id, String recipient);
    @Modifying
    @Query("update Notification n set n.isRead=true where n.recipient=:recipient and n.inAppVisible=true and n.isRead=false")
    int markAllVisibleAsRead(@Param("recipient") String recipient);
    List<Notification> findByEmailStatusAndEmailAttemptsLessThanOrderByTimestampAsc(EmailDeliveryStatus status,int attempts,Pageable pageable);
    List<Notification> findByRecipientAndEmailStatusOrderByTimestampAsc(String recipient,EmailDeliveryStatus status);
}
