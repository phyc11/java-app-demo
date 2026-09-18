package com.example.notification.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String recipient;

    @Column(nullable = false)
    private String title;

    @Column(length = 1000, nullable = false)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private NotificationType type;

    private Long resourceId;

    @Column(length = 40)
    private String resourceType;

    private boolean isRead = false;
    @Column(nullable=false) private boolean inAppVisible=true;
    @Column(nullable=false) private boolean dismissed=false;
    @Enumerated(EnumType.STRING) @Column(nullable=false,length=30)
    private EmailDeliveryStatus emailStatus = EmailDeliveryStatus.NOT_REQUESTED;
    private int emailAttempts;
    @Column(length=1000) private String emailLastError;
    private LocalDateTime emailSentAt;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @PrePersist
    protected void onCreate() {
        if (this.timestamp == null) {
            this.timestamp = LocalDateTime.now();
        }
    }

    public Notification() {}

    public Notification(String recipient, String title, String message, NotificationType type,
                        Long resourceId, String resourceType) {
        this.recipient = recipient;
        this.title = title;
        this.message = message;
        this.type = type;
        this.resourceId = resourceId;
        this.resourceType = resourceType;
        this.isRead = false;
        this.timestamp = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getRecipient() { return recipient; }
    public void setRecipient(String recipient) { this.recipient = recipient; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public NotificationType getType() { return type; }
    public void setType(NotificationType type) { this.type = type; }

    public Long getResourceId() { return resourceId; }
    public void setResourceId(Long resourceId) { this.resourceId = resourceId; }

    public String getResourceType() { return resourceType; }
    public void setResourceType(String resourceType) { this.resourceType = resourceType; }

    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }
    public boolean isInAppVisible(){return inAppVisible;} public void setInAppVisible(boolean v){inAppVisible=v;}
    public boolean isDismissed(){return dismissed;} public void setDismissed(boolean value){dismissed=value;}

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public EmailDeliveryStatus getEmailStatus(){return emailStatus;} public void setEmailStatus(EmailDeliveryStatus v){emailStatus=v;}
    public int getEmailAttempts(){return emailAttempts;} public void setEmailAttempts(int v){emailAttempts=v;} public String getEmailLastError(){return emailLastError;} public void setEmailLastError(String v){emailLastError=v;}
    public LocalDateTime getEmailSentAt(){return emailSentAt;} public void setEmailSentAt(LocalDateTime v){emailSentAt=v;}
}
