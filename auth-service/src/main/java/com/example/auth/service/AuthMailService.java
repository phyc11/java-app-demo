package com.example.auth.service;
import org.springframework.beans.factory.annotation.Value; import org.springframework.mail.SimpleMailMessage; import org.springframework.mail.javamail.JavaMailSender; import org.springframework.stereotype.Service;
@Service public class AuthMailService {private final JavaMailSender sender;private final boolean enabled;private final String from;
 public AuthMailService(JavaMailSender s,@Value("${auth.email.enabled:false}")boolean e,@Value("${auth.email.from:noreply@taskcraft.local}")String f){sender=s;enabled=e;from=f;}
 public void send(String to,String subject,String body){if(!enabled)return;SimpleMailMessage m=new SimpleMailMessage();m.setFrom(from);m.setTo(to);m.setSubject(subject);m.setText(body);sender.send(m);}}
