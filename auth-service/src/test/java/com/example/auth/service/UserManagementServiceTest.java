package com.example.auth.service;
import com.example.auth.dto.AdminUpdateUserRequest; import com.example.auth.model.*; import com.example.auth.repository.UserRepository;
import org.junit.jupiter.api.Test; import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*; import static org.mockito.Mockito.*;
class UserManagementServiceTest {
 @Test void adminCannotDeactivateSelf(){UserRepository repo=mock(UserRepository.class);TokenService tokens=mock(TokenService.class);User admin=user(1L,"admin",Role.ROLE_ADMIN);when(repo.findById(1L)).thenReturn(Optional.of(admin));UserManagementService service=new UserManagementService(repo,tokens);AdminUpdateUserRequest request=new AdminUpdateUserRequest();request.setActive(false);assertThrows(IllegalArgumentException.class,()->service.update(1L,request,"admin"));verify(tokens,never()).revokeAll(anyLong());}
 @Test void roleChangeRevokesAllSessions(){UserRepository repo=mock(UserRepository.class);TokenService tokens=mock(TokenService.class);User target=user(2L,"member",Role.ROLE_USER);when(repo.findById(2L)).thenReturn(Optional.of(target));when(repo.saveAndFlush(target)).thenReturn(target);UserManagementService service=new UserManagementService(repo,tokens);AdminUpdateUserRequest request=new AdminUpdateUserRequest();request.setRole(Role.ROLE_ADMIN);service.update(2L,request,"admin");verify(tokens).revokeAll(2L);assertEquals(Role.ROLE_ADMIN,target.getRole());}
 private User user(Long id,String name,Role role){User u=new User(name,"hash",name,role);u.setId(id);u.setEmail(name+"@test.local");u.setEmailVerified(true);return u;}
}
