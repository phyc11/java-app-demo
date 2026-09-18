package com.example.notification.dto;

import com.example.notification.model.EmailDeliveryStatus;
import com.example.notification.model.Notification;
import java.time.LocalDateTime;

public class NotificationDeliveryDTO {
    private Long notificationId;
    private String emailStatus;
    private int attempts;
    private int maxAttempts;
    private boolean retryable;
    private String lastError;
    private LocalDateTime sentAt;

    public NotificationDeliveryDTO() {}
    public NotificationDeliveryDTO(Notification notification,int maxAttempts) {
        notificationId=notification.getId();emailStatus=notification.getEmailStatus().name();
        attempts=notification.getEmailAttempts();this.maxAttempts=maxAttempts;
        retryable=(notification.getEmailStatus()==EmailDeliveryStatus.FAILED||notification.getEmailStatus()==EmailDeliveryStatus.SKIPPED_DISABLED)&&attempts<maxAttempts;
        lastError=notification.getEmailLastError();sentAt=notification.getEmailSentAt();
    }
    public Long getNotificationId(){return notificationId;} public void setNotificationId(Long value){notificationId=value;}
    public String getEmailStatus(){return emailStatus;} public void setEmailStatus(String value){emailStatus=value;}
    public int getAttempts(){return attempts;} public void setAttempts(int value){attempts=value;}
    public int getMaxAttempts(){return maxAttempts;} public void setMaxAttempts(int value){maxAttempts=value;}
    public boolean isRetryable(){return retryable;} public void setRetryable(boolean value){retryable=value;}
    public String getLastError(){return lastError;} public void setLastError(String value){lastError=value;}
    public LocalDateTime getSentAt(){return sentAt;} public void setSentAt(LocalDateTime value){sentAt=value;}
}
