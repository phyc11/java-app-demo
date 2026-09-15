package com.example.notification.model;

import javax.persistence.*;

@Entity
@Table(name = "notification_preferences", uniqueConstraints =
        @UniqueConstraint(name = "uk_notification_preference_user", columnNames = "username"))
public class NotificationPreference {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String username;

    @Column(length = 254)
    private String email;

    private boolean inAppEnabled = true;
    private boolean emailEnabled = false;
    private boolean mentionEnabled = true;
    private boolean replyEnabled = true;
    private boolean assignmentEnabled = true;
    private boolean deadlineEnabled = true;
    private boolean digestEmailEnabled = false;
    private Integer digestHour = 8;

    public NotificationPreference() {}

    public NotificationPreference(String username) { this.username = username; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public boolean isInAppEnabled() { return inAppEnabled; }
    public void setInAppEnabled(boolean inAppEnabled) { this.inAppEnabled = inAppEnabled; }
    public boolean isEmailEnabled() { return emailEnabled; }
    public void setEmailEnabled(boolean emailEnabled) { this.emailEnabled = emailEnabled; }
    public boolean isMentionEnabled() { return mentionEnabled; }
    public void setMentionEnabled(boolean mentionEnabled) { this.mentionEnabled = mentionEnabled; }
    public boolean isReplyEnabled() { return replyEnabled; }
    public void setReplyEnabled(boolean replyEnabled) { this.replyEnabled = replyEnabled; }
    public boolean isAssignmentEnabled() { return assignmentEnabled; }
    public void setAssignmentEnabled(boolean assignmentEnabled) { this.assignmentEnabled = assignmentEnabled; }
    public boolean isDeadlineEnabled() { return deadlineEnabled; }
    public void setDeadlineEnabled(boolean deadlineEnabled) { this.deadlineEnabled = deadlineEnabled; }
    public boolean isDigestEmailEnabled(){return digestEmailEnabled;} public void setDigestEmailEnabled(boolean v){digestEmailEnabled=v;}
    public Integer getDigestHour(){return digestHour;} public void setDigestHour(Integer v){digestHour=v;}
}
