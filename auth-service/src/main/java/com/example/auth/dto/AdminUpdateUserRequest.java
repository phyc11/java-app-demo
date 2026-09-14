package com.example.auth.dto;
import com.example.auth.model.Role; import javax.validation.constraints.Email; import javax.validation.constraints.Size;
public class AdminUpdateUserRequest {
 @Email private String email; @Size(max=255) private String fullName; private Role role; private Boolean active; private Boolean emailVerified; private Long version;
 public String getEmail(){return email;} public void setEmail(String v){email=v;} public String getFullName(){return fullName;} public void setFullName(String v){fullName=v;}
 public Role getRole(){return role;} public void setRole(Role v){role=v;} public Boolean getActive(){return active;} public void setActive(Boolean v){active=v;}
 public Boolean getEmailVerified(){return emailVerified;} public void setEmailVerified(Boolean v){emailVerified=v;} public Long getVersion(){return version;} public void setVersion(Long v){version=v;}
}
