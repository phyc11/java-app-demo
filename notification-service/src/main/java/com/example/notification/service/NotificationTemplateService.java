package com.example.notification.service;
import com.example.notification.dto.NotificationEvent; import com.example.notification.model.*; import com.example.notification.repository.NotificationTemplateRepository; import org.springframework.stereotype.Service; import java.util.*;
@Service public class NotificationTemplateService {
 private final NotificationTemplateRepository repo; public NotificationTemplateService(NotificationTemplateRepository r){repo=r;}
 public Rendered render(NotificationEvent e,NotificationType type,String fallbackTitle){NotificationTemplate t=repo.findById(type.name()).filter(NotificationTemplate::isActive).orElse(null);String subject=t==null?fallbackTitle:t.getSubjectTemplate();String body=t==null?e.getMessage():t.getBodyTemplate();return new Rendered(expand(subject,e),expand(body,e));}
 public List<NotificationTemplate> list(){return repo.findAll();} public NotificationTemplate save(NotificationTemplate t){if(t.getCode()==null||t.getCode().trim().isEmpty())throw new IllegalArgumentException("Template code is required");if(t.getSubjectTemplate()==null||t.getBodyTemplate()==null)throw new IllegalArgumentException("Subject and body are required");t.setCode(t.getCode().trim().toUpperCase());return repo.save(t);}
 private String expand(String s,NotificationEvent e){return s.replace("{actor}",value(e.getActor())).replace("{message}",value(e.getMessage())).replace("{recipient}",value(e.getRecipient())).replace("{resourceType}",value(e.getResourceType())).replace("{resourceId}",e.getResourceId()==null?"":e.getResourceId().toString());}private String value(String v){return v==null?"":v;}
 public static class Rendered{public final String subject,body;Rendered(String s,String b){subject=s;body=b;}}
}
