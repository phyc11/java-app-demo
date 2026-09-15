package com.example.notification.model;
import javax.persistence.*;
@Entity @Table(name="notification_templates") public class NotificationTemplate {
 @Id @Column(length=50) private String code; @Column(nullable=false) private String subjectTemplate; @Column(nullable=false,length=2000) private String bodyTemplate; private boolean active=true;
 public NotificationTemplate(){} public NotificationTemplate(String c,String s,String b){code=c;subjectTemplate=s;bodyTemplate=b;}
 public String getCode(){return code;}public void setCode(String v){code=v;}public String getSubjectTemplate(){return subjectTemplate;}public void setSubjectTemplate(String v){subjectTemplate=v;}public String getBodyTemplate(){return bodyTemplate;}public void setBodyTemplate(String v){bodyTemplate=v;}public boolean isActive(){return active;}public void setActive(boolean v){active=v;}
}
