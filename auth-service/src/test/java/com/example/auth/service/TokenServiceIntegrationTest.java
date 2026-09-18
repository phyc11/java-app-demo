package com.example.auth.service;
import com.example.auth.dto.RegisterRequest; import com.example.auth.model.*; import com.example.auth.repository.*;
import org.junit.jupiter.api.*; import org.springframework.beans.factory.annotation.Autowired; import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest(properties="auth.email.enabled=false")
class TokenServiceIntegrationTest {
 @Autowired TokenService tokens; @Autowired RefreshTokenRepository refresh; @Autowired UserActionTokenRepository actions; @Autowired UserRepository users; @Autowired AuthService auth;
 @BeforeEach void clean(){refresh.deleteAll();actions.deleteAll();users.deleteAll();}
 @Test void refreshRotationInvalidatesOldToken(){String first=tokens.issueRefresh(10L);TokenService.Rotation next=tokens.rotate(first);assertEquals(10L,next.userId);assertThrows(IllegalArgumentException.class,()->tokens.rotate(first));assertDoesNotThrow(()->tokens.rotate(next.token));}
 @Test void logoutRevokesRefreshToken(){String raw=tokens.issueRefresh(10L);tokens.revoke(raw);assertThrows(IllegalArgumentException.class,()->tokens.rotate(raw));}
 @Test void actionTokenCanOnlyBeConsumedOnce(){String raw=tokens.action(10L,UserActionToken.Type.EMAIL_VERIFICATION,10);assertEquals(10L,tokens.consume(raw,UserActionToken.Type.EMAIL_VERIFICATION));assertThrows(IllegalArgumentException.class,()->tokens.consume(raw,UserActionToken.Type.EMAIL_VERIFICATION));}
 @Test void registrationNeverAllowsSelfAssignedAdmin(){RegisterRequest r=new RegisterRequest();r.setUsername("alice");r.setPassword("password123");r.setFullName("Alice");r.setEmail("alice@example.com");r.setRole(Role.ROLE_ADMIN);auth.register(r);User u=users.findByUsername("alice").orElseThrow();assertEquals(Role.ROLE_USER,u.getRole());assertFalse(u.isEmailVerified());}
 @Test void duplicateEmailIsRejected(){RegisterRequest a=request("alice");auth.register(a);RegisterRequest b=request("bob");b.setEmail("ALICE@example.com");assertThrows(IllegalArgumentException.class,()->auth.register(b));}
 @Test void listsAndRevokesOnlyOwnedSessions(){auth.register(request("alice"));auth.register(request("bob"));Long alice=users.findByUsername("alice").orElseThrow().getId(),bob=users.findByUsername("bob").orElseThrow().getId();tokens.issueRefresh(alice);tokens.issueRefresh(alice);tokens.issueRefresh(bob);assertEquals(2,tokens.sessions(alice).size());Long aliceSession=tokens.sessions(alice).get(0).getId(),bobSession=tokens.sessions(bob).get(0).getId();assertThrows(IllegalArgumentException.class,()->tokens.revokeSession(alice,bobSession));tokens.revokeSession(alice,aliceSession);assertFalse(tokens.sessions(alice).get(0).isActive());assertNotNull(tokens.sessions(alice).get(0).getRevokedAt());}
 @Test void logoutAllRevokesEveryActiveSession(){auth.register(request("alice"));Long userId=users.findByUsername("alice").orElseThrow().getId();tokens.issueRefresh(userId);tokens.issueRefresh(userId);auth.logoutAll("alice");assertTrue(tokens.sessions(userId).stream().noneMatch(com.example.auth.dto.AuthSessionDTO::isActive));}
 private RegisterRequest request(String name){RegisterRequest r=new RegisterRequest();r.setUsername(name);r.setPassword("password123");r.setFullName(name);r.setEmail(name+"@example.com");return r;}
}
