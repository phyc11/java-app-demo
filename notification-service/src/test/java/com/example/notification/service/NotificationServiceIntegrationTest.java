package com.example.notification.service;

import com.example.notification.dto.*;
import com.example.notification.messaging.NotificationEventConsumer;
import com.example.notification.model.*;
import com.example.notification.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
import java.util.Set;

@SpringBootTest(properties = {
        "notification.email.enabled=false",
        "notification.rabbitmq.enabled=false"
})
class NotificationServiceIntegrationTest {
    @Autowired private NotificationService notificationService;
    @Autowired private NotificationRepository notificationRepository;
    @Autowired private NotificationPreferenceRepository preferenceRepository;
    @Autowired private ProcessedEventRepository processedEventRepository;
    @Autowired private NotificationTemplateRepository templateRepository;

    @BeforeEach
    void cleanDatabase() {
        processedEventRepository.deleteAll();
        notificationRepository.deleteAll();
        preferenceRepository.deleteAll();
        templateRepository.deleteAll();
    }

    @Test
    void emptyInboxDoesNotCreateSeedNotifications() {
        assertTrue(notificationService.getUserNotifications("alice").isEmpty());
        assertEquals(0, notificationRepository.count());
    }

    @Test
    void eventCreatesUnreadNotificationWithResourceLink() {
        NotificationEvent event = event("evt-1", "MENTION", "alice");
        event.setResourceId(42L);
        event.setResourceType("COMMENT");

        NotificationDTO result = notificationService.processEvent(event);

        assertNotNull(result);
        assertFalse(result.isRead());
        assertEquals("MENTION", result.getType());
        assertEquals(42L, result.getResourceId());
        assertEquals(1, notificationService.getUnreadCount("alice"));
    }

    @Test
    void preferenceCanDisableOneNotificationType() {
        NotificationPreferenceRequest request = new NotificationPreferenceRequest();
        request.setMentionEnabled(false);
        request.setAssignmentEnabled(true);
        notificationService.updatePreference("alice", request);

        assertNull(notificationService.processEvent(event("evt-1", "MENTION", "alice")));
        assertNotNull(notificationService.processEvent(event("evt-2", "TASK_ASSIGNED", "alice")));
        assertEquals(1, notificationRepository.count());
    }

    @Test
    void enablingEmailRequiresAValidAddress() {
        NotificationPreferenceRequest missingEmail = new NotificationPreferenceRequest();
        missingEmail.setEmailEnabled(true);
        assertThrows(IllegalArgumentException.class,
                () -> notificationService.updatePreference("alice", missingEmail));

        NotificationPreferenceRequest valid = new NotificationPreferenceRequest();
        valid.setEmail("alice@example.com");
        valid.setEmailEnabled(true);
        NotificationPreference saved = notificationService.updatePreference("alice", valid);
        assertTrue(saved.isEmailEnabled());
        assertEquals("alice@example.com", saved.getEmail());
    }

    @Test
    void userCannotMarkAnotherUsersNotificationAsRead() {
        NotificationDTO notification = notificationService.processEvent(event("evt-1", "COMMENT_REPLY", "alice"));

        assertThrows(RuntimeException.class,
                () -> notificationService.markAsRead(notification.getId(), "bob"));
        assertTrue(notificationService.markAsRead(notification.getId(), "alice").isRead());
    }

    @Test
    void markAllOnlyAffectsRequestedUser() {
        notificationService.processEvent(event("evt-1", "TASK_DUE_SOON", "alice"));
        notificationService.processEvent(event("evt-2", "TASK_OVERDUE", "alice"));
        notificationService.processEvent(event("evt-3", "TASK_ASSIGNED", "bob"));

        notificationService.markAllAsRead("alice");

        assertEquals(0, notificationService.getUnreadCount("alice"));
        assertEquals(1, notificationService.getUnreadCount("bob"));
    }

    @Test
    void consumerIgnoresRedeliveredEvent() {
        NotificationEventConsumer consumer = new NotificationEventConsumer(notificationService, processedEventRepository);
        NotificationEvent event = event("evt-duplicate", "TASK_ASSIGNED", "alice");

        consumer.consume(event);
        consumer.consume(event);

        assertEquals(1, notificationRepository.count());
        assertEquals(1, processedEventRepository.count());
    }

    @Test
    void templateRendersEventVariables() {
        templateRepository.save(new NotificationTemplate("MENTION", "{actor} mentioned you", "{message} on {resourceType} #{resourceId}"));
        NotificationEvent event=event("evt-template","MENTION","alice"); event.setActor("bob"); event.setResourceType("COMMENT"); event.setResourceId(9L);
        NotificationDTO result=notificationService.processEvent(event);
        assertEquals("bob mentioned you",result.getTitle());
        assertEquals("Test event message on COMMENT #9",result.getMessage());
    }

    @Test
    void digestPreferenceQueuesEmailInsteadOfSendingImmediately() {
        NotificationPreferenceRequest request=new NotificationPreferenceRequest(); request.setEmail("alice@example.com"); request.setEmailEnabled(true); request.setDigestEmailEnabled(true); notificationService.updatePreference("alice",request);
        notificationService.processEvent(event("evt-digest","TASK_ASSIGNED","alice"));
        assertEquals(EmailDeliveryStatus.PENDING_DIGEST,notificationRepository.findAll().get(0).getEmailStatus());
    }

    @Test
    void inboxCanFilterUnreadNotificationsByType() {
        NotificationDTO mention=notificationService.processEvent(event("evt-filter-1","MENTION","alice"));
        notificationService.processEvent(event("evt-filter-2","TASK_ASSIGNED","alice"));
        notificationService.markAsRead(mention.getId(),"alice");

        assertEquals(0,notificationService.getUserNotifications("alice","MENTION",true,0,20).getTotalElements());
        assertEquals(1,notificationService.getUserNotifications("alice","TASK_ASSIGNED",true,0,20).getTotalElements());
    }

    @Test
    void dismissOnlyHidesNotificationFromItsOwnerInbox() {
        NotificationDTO notification=notificationService.processEvent(event("evt-dismiss","COMMENT_REPLY","alice"));

        assertThrows(RuntimeException.class,()->notificationService.dismiss(notification.getId(),"bob"));
        notificationService.dismiss(notification.getId(),"alice");

        assertTrue(notificationService.getUserNotifications("alice").isEmpty());
        assertEquals(1,notificationRepository.count());
    }

    @Test
    void summaryGroupsUnreadNotificationsByType() {
        NotificationDTO mention=notificationService.processEvent(event("evt-summary-1","MENTION","alice"));
        notificationService.processEvent(event("evt-summary-2","MENTION","alice"));
        notificationService.processEvent(event("evt-summary-3","TASK_ASSIGNED","alice"));
        notificationService.markAsRead(mention.getId(),"alice");

        NotificationSummaryDTO summary=notificationService.getSummary("alice");

        assertEquals(3,summary.getTotal());assertEquals(2,summary.getUnread());
        assertEquals(1L,summary.getUnreadByType().get("MENTION"));
        assertEquals(1L,summary.getUnreadByType().get("TASK_ASSIGNED"));
    }

    @Test
    void bulkReadAndDismissOnlyOperateOnOwnedNotifications() {
        NotificationDTO first=notificationService.processEvent(event("evt-bulk-1","MENTION","alice"));
        NotificationDTO second=notificationService.processEvent(event("evt-bulk-2","TASK_ASSIGNED","alice"));
        NotificationDTO other=notificationService.processEvent(event("evt-bulk-3","TASK_ASSIGNED","bob"));
        NotificationBulkRequest owned=new NotificationBulkRequest();owned.setNotificationIds(Set.of(first.getId(),second.getId()));

        assertEquals(2,notificationService.bulkMarkAsRead("alice",owned));
        assertEquals(0,notificationService.getUnreadCount("alice"));
        assertEquals(2,notificationService.bulkDismiss("alice",owned));
        assertTrue(notificationService.getUserNotifications("alice").isEmpty());

        NotificationBulkRequest mixed=new NotificationBulkRequest();mixed.setNotificationIds(Set.of(first.getId(),other.getId()));
        assertThrows(SecurityException.class,()->notificationService.bulkMarkAsRead("alice",mixed));
        assertEquals(1,notificationService.getUnreadCount("bob"));
    }

    @Test
    void dismissedInboxCanRestoreNotificationWithoutIncludingEmailOnlyRecords() {
        NotificationDTO visible=notificationService.processEvent(event("evt-restore","MENTION","alice"));
        notificationService.dismiss(visible.getId(),"alice");
        NotificationPreferenceRequest hidden=new NotificationPreferenceRequest();hidden.setInAppEnabled(false);
        notificationService.updatePreference("bob",hidden);
        assertNull(notificationService.processEvent(event("evt-email-only","SYSTEM","bob")));

        assertEquals(1,notificationService.getDismissed("alice",0,20).getTotalElements());
        assertEquals(0,notificationService.getDismissed("bob",0,20).getTotalElements());
        assertThrows(RuntimeException.class,()->notificationService.restore(visible.getId(),"bob"));
        assertEquals(visible.getId(),notificationService.restore(visible.getId(),"alice").getId());
        assertEquals(1,notificationService.getUserNotifications("alice").size());
    }

    @Test
    void deliveryDetailsAreScopedAndExposeRetryState() {
        NotificationPreferenceRequest preference=new NotificationPreferenceRequest();preference.setEmail("alice@example.com");preference.setEmailEnabled(true);
        notificationService.updatePreference("alice",preference);
        NotificationDTO notification=notificationService.processEvent(event("evt-delivery","TASK_ASSIGNED","alice"));

        NotificationDeliveryDTO delivery=notificationService.getDelivery(notification.getId(),"alice");

        assertEquals("SKIPPED_DISABLED",delivery.getEmailStatus());
        assertTrue(delivery.isRetryable());assertEquals(3,delivery.getMaxAttempts());
        assertThrows(RuntimeException.class,()->notificationService.getDelivery(notification.getId(),"bob"));
    }

    private NotificationEvent event(String id, String type, String recipient) {
        NotificationEvent event = new NotificationEvent();
        event.setEventId(id);
        event.setType(type);
        event.setRecipient(recipient);
        event.setMessage("Test event message");
        return event;
    }
}
