package com.example.auth.service;
import com.example.auth.dto.*; import com.example.auth.model.*; import com.example.auth.repository.UserRepository; import com.example.common.exception.ResourceNotFoundException;
import org.springframework.data.domain.*; import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional;
import java.util.Locale;
@Service public class UserManagementService {
 private final UserRepository users; private final TokenService tokens;
 public UserManagementService(UserRepository users,TokenService tokens){this.users=users;this.tokens=tokens;}
 public Page<UserDTO> list(String search,Boolean active,Role role,int page,int size,String sort){int safeSize=Math.min(Math.max(size,1),100);String safeSort=("username".equals(sort)||"email".equals(sort)||"createdAt".equals(sort))?sort:"createdAt";String q=search==null||search.trim().isEmpty()?null:search.trim();return users.search(q,active,role,PageRequest.of(Math.max(page,0),safeSize,Sort.by(safeSort).ascending())).map(UserDTO::new);}
 public UserDTO get(Long id){return new UserDTO(find(id));}
 @Transactional public UserDTO update(Long id,AdminUpdateUserRequest r,String actor){User u=find(id);if(r.getVersion()!=null&&!r.getVersion().equals(u.getVersion()))throw new IllegalStateException("User was modified; reload and try again");boolean securityChanged=false;
  if(r.getEmail()!=null&&!r.getEmail().trim().equalsIgnoreCase(u.getEmail())){String email=r.getEmail().trim().toLowerCase(Locale.ROOT);if(users.existsByEmailIgnoreCase(email))throw new IllegalArgumentException("Email already exists");u.setEmail(email);u.setEmailVerified(false);securityChanged=true;}
  if(r.getFullName()!=null)u.setFullName(r.getFullName().trim());if(r.getEmailVerified()!=null)u.setEmailVerified(r.getEmailVerified());
  if(r.getRole()!=null&&r.getRole()!=u.getRole()){assertNotSelf(actor,u,"change your own role");ensureLastAdmin(u);u.setRole(r.getRole());securityChanged=true;}
  if(r.getActive()!=null&&r.getActive()!=u.isActive()){if(!r.getActive()){assertNotSelf(actor,u,"deactivate yourself");ensureLastAdmin(u);}u.setActive(r.getActive());securityChanged=true;}
  User saved=users.saveAndFlush(u);if(securityChanged)tokens.revokeAll(saved.getId());return new UserDTO(saved);}
 @Transactional public void deactivate(Long id,String actor){AdminUpdateUserRequest r=new AdminUpdateUserRequest();r.setActive(false);update(id,r,actor);}
 private User find(Long id){return users.findById(id).orElseThrow(()->new ResourceNotFoundException("User","id",id));}
 private void assertNotSelf(String actor,User target,String action){if(target.getUsername().equalsIgnoreCase(actor))throw new IllegalArgumentException("You cannot "+action);}
 private void ensureLastAdmin(User u){if(u.getRole()==Role.ROLE_ADMIN&&u.isActive()&&users.countByRoleAndActiveTrue(Role.ROLE_ADMIN)<=1)throw new IllegalArgumentException("Cannot deactivate or demote the last active admin");}
}
