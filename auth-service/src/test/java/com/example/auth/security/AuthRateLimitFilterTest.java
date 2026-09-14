package com.example.auth.security;
import org.junit.jupiter.api.Test; import org.springframework.mock.web.*; import javax.servlet.*; import java.util.concurrent.atomic.AtomicInteger; import static org.junit.jupiter.api.Assertions.*;
class AuthRateLimitFilterTest {
 @Test void limitsLoginAfterTenRequests()throws Exception{AuthRateLimitFilter f=new AuthRateLimitFilter();AtomicInteger passed=new AtomicInteger();FilterChain chain=(req,res)->passed.incrementAndGet();for(int i=0;i<11;i++){MockHttpServletRequest req=new MockHttpServletRequest("POST","/api/auth/login");req.setRemoteAddr("10.0.0.1");f.doFilter(req,new MockHttpServletResponse(),chain);}assertEquals(10,passed.get());}
}
