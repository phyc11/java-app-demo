package com.example.notification.service;

import com.example.common.exception.ResourceNotFoundException;
import com.example.notification.dto.*;
import com.example.notification.model.*;
import com.example.notification.repository.NotificationPreferenceRepository;
import com.example.notification.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class NotificationService {
    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

    private final NotificationRepository notificationRepository;
    private final NotificationPreferenceRepository preferenceRepository;
    private final JavaMailSender mailSender;
    private final boolean emailDeliveryEnabled;
    private final String mailFrom;
    private final NotificationTemplateService templateService;
    private final int maxEmailAttempts;
    private final Map<String, List<SseEmitter>> emittersMap = new ConcurrentHashMap<>();

    public NotificationService(NotificationRepository notificationRepository,
                               NotificationPreferenceRepository preferenceRepository,
                               JavaMailSender mailSender,
                               NotificationTemplateService templateService,
                               @Value("${notification.email.enabled:false}") boolean emailDeliveryEnabled,
                               @Value("${notification.email.from:noreply@taskcraft.local}") String mailFrom,
                               @Value("${notification.email.max-attempts:3}") int maxEmailAttempts) {
        this.notificationRepository = notificationRepository;
        this.preferenceRepository = preferenceRepository;
        this.mailSender = mailSender;
        this.emailDeliveryEnabled = emailDeliveryEnabled;
        this.mailFrom = mailFrom;
        this.templateService=templateService; this.maxEmailAttempts=maxEmailAttempts;
    }

    public List<NotificationDTO> getUserNotifications(String recipient) {
        return notificationRepository.findByRecipientAndInAppVisibleTrueOrderByTimestampDesc(requireUser(recipient)).stream()
                .map(NotificationDTO::new)
                .collect(Collectors.toList());
    }

    public Page<NotificationDTO> getUserNotifications(String recipient,int page,int size) {
        if(page<0||size<1||size>100)throw new IllegalArgumentException("page must be >= 0 and size must be 1-100");
        return notificationRepository.findByRecipientAndInAppVisibleTrue(requireUser(recipient),PageRequest.of(page,size,Sort.by("timestamp").descending())).map(NotificationDTO::new);
    }

    public long getUnreadCount(String recipient) {
        return notificationRepository.countByRecipientAndIsReadFalseAndInAppVisibleTrue(requireUser(recipient));
    }

    @Transactional
    public NotificationDTO sendNotification(String recipient, String title, String message, String type) {
        return deliver(requireUser(recipient), required(title, "Title"), required(message, "Message"),
                parseType(type), null, null, true);
    }

    @Transactional
    public NotificationDTO processEvent(NotificationEvent event) {
        if (event == null) throw new IllegalArgumentException("Notification event is required");
        String recipient = requireUser(event.getRecipient());
        NotificationType type = parseType(event.getType());
        NotificationPreference preference = getOrCreatePreference(recipient);
        if (!isTypeEnabled(preference, type)) {
            log.debug("Notification type {} disabled for {}", type, recipient);
            return null;
        }
        NotificationTemplateService.Rendered rendered=templateService.render(event,type,defaultTitle(event,type));
        return deliver(recipient, rendered.subject, required(rendered.body, "Message"),
                type, event.getResourceId(), event.getResourceType(), false);
    }

    @Transactional
    public NotificationDTO markAsRead(Long id, String recipient) {
        Notification notification = notificationRepository.findByIdAndRecipient(id, requireUser(recipient))
                .orElseThrow(() -> new ResourceNotFoundException("Notification", "id", id));
        notification.setRead(true);
        return new NotificationDTO(notificationRepository.save(notification));
    }

    @Transactional
    public void markAllAsRead(String recipient) {
        List<Notification> notifications = notificationRepository
                .findByRecipientOrderByTimestampDesc(requireUser(recipient));
        notifications.stream().filter(n -> !n.isRead()).forEach(n -> n.setRead(true));
        notificationRepository.saveAll(notifications);
    }

    public NotificationPreference getPreference(String username) {
        return getOrCreatePreference(requireUser(username));
    }

    @Transactional
    public NotificationPreference updatePreference(String username, NotificationPreferenceRequest request) {
        String user = requireUser(username);
        if (request == null) throw new IllegalArgumentException("Preference body is required");
        NotificationPreference preference = getOrCreatePreference(user);
        if (request.getEmail() != null) {
            String email = request.getEmail().trim().toLowerCase(Locale.ROOT);
            if (!email.isEmpty() && !EMAIL_PATTERN.matcher(email).matches()) {
                throw new IllegalArgumentException("Invalid email address");
            }
            preference.setEmail(email.isEmpty() ? null : email);
        }
        if (request.getInAppEnabled() != null) preference.setInAppEnabled(request.getInAppEnabled());
        if (request.getEmailEnabled() != null) preference.setEmailEnabled(request.getEmailEnabled());
        if (request.getMentionEnabled() != null) preference.setMentionEnabled(request.getMentionEnabled());
        if (request.getReplyEnabled() != null) preference.setReplyEnabled(request.getReplyEnabled());
        if (request.getAssignmentEnabled() != null) preference.setAssignmentEnabled(request.getAssignmentEnabled());
        if (request.getDeadlineEnabled() != null) preference.setDeadlineEnabled(request.getDeadlineEnabled());
        if(request.getDigestEmailEnabled()!=null)preference.setDigestEmailEnabled(request.getDigestEmailEnabled());
        if(request.getDigestHour()!=null){if(request.getDigestHour()<0||request.getDigestHour()>23)throw new IllegalArgumentException("Digest hour must be between 0 and 23");preference.setDigestHour(request.getDigestHour());}
        if (preference.isEmailEnabled() && preference.getEmail() == null) {
            throw new IllegalArgumentException("Email address is required when email notifications are enabled");
        }
        return preferenceRepository.save(preference);
    }

    public SseEmitter subscribeSse(String recipient) {
        String user = requireUser(recipient);
        SseEmitter emitter = new SseEmitter(3600000L);
        emittersMap.computeIfAbsent(user, key -> Collections.synchronizedList(new ArrayList<>())).add(emitter);
        emitter.onCompletion(() -> removeEmitter(user, emitter));
        emitter.onTimeout(() -> removeEmitter(user, emitter));
        emitter.onError(error -> removeEmitter(user, emitter));
        try {
            emitter.send(SseEmitter.event().name("INIT").data("Connected"));
        } catch (IOException exception) {
            removeEmitter(user, emitter);
        }
        return emitter;
    }

    @Transactional
    public NotificationDTO retryEmail(Long id,String recipient){Notification n=notificationRepository.findByIdAndRecipient(id,requireUser(recipient)).orElseThrow(()->new ResourceNotFoundException("Notification","id",id));if(n.getEmailStatus()!=EmailDeliveryStatus.FAILED&&n.getEmailStatus()!=EmailDeliveryStatus.SKIPPED_DISABLED)throw new IllegalStateException("Email is not retryable");if(n.getEmailAttempts()>=maxEmailAttempts)throw new IllegalStateException("Maximum email attempts reached");NotificationPreference p=getOrCreatePreference(recipient);if(!p.isEmailEnabled()||p.getEmail()==null)throw new IllegalStateException("Email notifications are disabled");n.setEmailStatus(EmailDeliveryStatus.PENDING);notificationRepository.save(n);attemptEmail(n,p.getEmail());return new NotificationDTO(n);}

    @Scheduled(fixedDelayString="${notification.sse.heartbeat-interval-ms:25000}")
    public void heartbeat(){emittersMap.forEach((recipient,emitters)->{synchronized(emitters){Iterator<SseEmitter> it=emitters.iterator();while(it.hasNext())try{it.next().send(SseEmitter.event().name("HEARTBEAT").data(java.time.Instant.now().toString()));}catch(Exception e){it.remove();}}});}

    private NotificationDTO deliver(String recipient, String title, String message, NotificationType type,
                                    Long resourceId, String resourceType, boolean forceInApp) {
        NotificationPreference preference = getOrCreatePreference(recipient);
        boolean visible=forceInApp||preference.isInAppEnabled();
        Notification saved=new Notification(recipient,title,message,type,resourceId,resourceType);saved.setInAppVisible(visible);
        if(preference.isEmailEnabled())saved.setEmailStatus(preference.isDigestEmailEnabled()?EmailDeliveryStatus.PENDING_DIGEST:EmailDeliveryStatus.PENDING);
        saved=notificationRepository.save(saved);NotificationDTO dto=visible?new NotificationDTO(saved):null;if(dto!=null)pushSseEvent(recipient,dto);
        if(saved.getEmailStatus()==EmailDeliveryStatus.PENDING)attemptEmail(saved,preference.getEmail());
        return dto;
    }

    private void attemptEmail(Notification notification,String address) {
        if (!emailDeliveryEnabled) {
            log.info("Email delivery disabled; skipped notification for {}", address);
            notification.setEmailStatus(EmailDeliveryStatus.SKIPPED_DISABLED);notificationRepository.save(notification);
            return;
        }
        try {
            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setFrom(mailFrom);
            mail.setTo(address);
            mail.setSubject(notification.getTitle());
            mail.setText(notification.getMessage());
            mailSender.send(mail);
            notification.setEmailAttempts(notification.getEmailAttempts()+1);notification.setEmailStatus(EmailDeliveryStatus.SENT);notification.setEmailSentAt(java.time.LocalDateTime.now());notification.setEmailLastError(null);notificationRepository.save(notification);
        } catch (RuntimeException exception) {
            log.error("Could not send notification email to {}", address, exception);
            notification.setEmailAttempts(notification.getEmailAttempts()+1);notification.setEmailStatus(EmailDeliveryStatus.FAILED);notification.setEmailLastError(exception.getMessage());notificationRepository.save(notification);
        }
    }

    @Scheduled(fixedDelayString="${notification.email.retry-interval-ms:60000}")
    public void retryFailedEmail(){for(Notification n:notificationRepository.findByEmailStatusAndEmailAttemptsLessThanOrderByTimestampAsc(EmailDeliveryStatus.FAILED,maxEmailAttempts,PageRequest.of(0,100))){NotificationPreference p=getOrCreatePreference(n.getRecipient());if(p.isEmailEnabled()&&p.getEmail()!=null)attemptEmail(n,p.getEmail());}}

    @Scheduled(cron="${notification.digest.cron:0 0 * * * *}")
    public void sendDigests(){int hour=java.time.LocalDateTime.now().getHour();for(NotificationPreference p:preferenceRepository.findAll()){if(!p.isEmailEnabled()||!p.isDigestEmailEnabled()||p.getDigestHour()==null||p.getDigestHour()!=hour||p.getEmail()==null)continue;List<Notification> pending=notificationRepository.findByRecipientAndEmailStatusOrderByTimestampAsc(p.getUsername(),EmailDeliveryStatus.PENDING_DIGEST);if(pending.isEmpty())continue;Notification digest=new Notification(p.getUsername(),"TaskCraft notification digest",pending.stream().map(n->"- "+n.getTitle()+": "+n.getMessage()).collect(Collectors.joining("\n")),NotificationType.SYSTEM,null,"DIGEST");digest.setInAppVisible(false);digest.setEmailStatus(EmailDeliveryStatus.PENDING);attemptEmail(digest,p.getEmail());if(digest.getEmailStatus()==EmailDeliveryStatus.SENT){pending.forEach(n->{n.setEmailStatus(EmailDeliveryStatus.DIGESTED);n.setEmailSentAt(java.time.LocalDateTime.now());});notificationRepository.saveAll(pending);}}}

    private void pushSseEvent(String recipient, NotificationDTO dto) {
        List<SseEmitter> emitters = emittersMap.get(recipient);
        if (emitters == null) return;
        synchronized (emitters) {
            Iterator<SseEmitter> iterator = emitters.iterator();
            while (iterator.hasNext()) {
                try {
                    iterator.next().send(SseEmitter.event().name("NOTIFICATION").data(dto));
                } catch (Exception exception) {
                    iterator.remove();
                }
            }
        }
    }

    private void removeEmitter(String recipient, SseEmitter emitter) {
        List<SseEmitter> emitters = emittersMap.get(recipient);
        if (emitters != null) {
            emitters.remove(emitter);
            if (emitters.isEmpty()) emittersMap.remove(recipient);
        }
    }

    private NotificationPreference getOrCreatePreference(String username) {
        return preferenceRepository.findByUsername(username)
                .orElseGet(() -> preferenceRepository.save(new NotificationPreference(username)));
    }

    private boolean isTypeEnabled(NotificationPreference preference, NotificationType type) {
        switch (type) {
            case MENTION: return preference.isMentionEnabled();
            case COMMENT_REPLY: return preference.isReplyEnabled();
            case TASK_ASSIGNED: return preference.isAssignmentEnabled();
            case TASK_DUE_SOON:
            case TASK_OVERDUE: return preference.isDeadlineEnabled();
            default: return true;
        }
    }

    private String defaultTitle(NotificationEvent event, NotificationType type) {
        if (event.getTitle() != null && !event.getTitle().trim().isEmpty()) return event.getTitle().trim();
        switch (type) {
            case MENTION: return "You were mentioned";
            case COMMENT_REPLY: return "New reply to your comment";
            case TASK_ASSIGNED: return "Task assigned to you";
            case TASK_DUE_SOON: return "Task due soon";
            case TASK_OVERDUE: return "Task overdue";
            default: return "TaskCraft notification";
        }
    }

    private NotificationType parseType(String value) {
        try {
            return NotificationType.valueOf(required(value, "Notification type").toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("Invalid notification type: " + value);
        }
    }

    private String requireUser(String value) { return required(value, "Recipient"); }

    private String required(String value, String field) {
        if (value == null || value.trim().isEmpty()) throw new IllegalArgumentException(field + " is required");
        return value.trim();
    }
}
