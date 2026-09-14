package com.example.notification.dto;

public class NotificationPreferenceRequest {
    private String email;
    private Boolean inAppEnabled;
    private Boolean emailEnabled;
    private Boolean mentionEnabled;
    private Boolean replyEnabled;
    private Boolean assignmentEnabled;
    private Boolean deadlineEnabled;

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Boolean getInAppEnabled() { return inAppEnabled; }
    public void setInAppEnabled(Boolean inAppEnabled) { this.inAppEnabled = inAppEnabled; }
    public Boolean getEmailEnabled() { return emailEnabled; }
    public void setEmailEnabled(Boolean emailEnabled) { this.emailEnabled = emailEnabled; }
    public Boolean getMentionEnabled() { return mentionEnabled; }
    public void setMentionEnabled(Boolean mentionEnabled) { this.mentionEnabled = mentionEnabled; }
    public Boolean getReplyEnabled() { return replyEnabled; }
    public void setReplyEnabled(Boolean replyEnabled) { this.replyEnabled = replyEnabled; }
    public Boolean getAssignmentEnabled() { return assignmentEnabled; }
    public void setAssignmentEnabled(Boolean assignmentEnabled) { this.assignmentEnabled = assignmentEnabled; }
    public Boolean getDeadlineEnabled() { return deadlineEnabled; }
    public void setDeadlineEnabled(Boolean deadlineEnabled) { this.deadlineEnabled = deadlineEnabled; }
}
