package com.example.auth.security;
import org.springframework.http.MediaType; import org.springframework.stereotype.Component; import org.springframework.web.filter.OncePerRequestFilter;
import javax.servlet.*; import javax.servlet.http.*; import java.io.IOException; import java.time.Instant; import java.util.concurrent.ConcurrentHashMap;
@Component public class AuthRateLimitFilter extends OncePerRequestFilter {
 private final ConcurrentHashMap<String,Window> windows=new ConcurrentHashMap<>();
 @Override protected void doFilterInternal(HttpServletRequest req,HttpServletResponse res,FilterChain chain)throws ServletException,IOException{
  String path=req.getRequestURI();if(!"POST".equals(req.getMethod())||(!path.endsWith("/login")&&!path.endsWith("/register"))){chain.doFilter(req,res);return;}
  String key=req.getRemoteAddr()+":"+path;long now=Instant.now().getEpochSecond();Window w=windows.compute(key,(k,old)->old==null||now-old.start>=60?new Window(now):old.increment());
  if(w.count>10){res.setStatus(429);res.setContentType(MediaType.APPLICATION_JSON_VALUE);res.getWriter().write("{\"success\":false,\"message\":\"Too many authentication attempts. Try again later.\"}");return;}chain.doFilter(req,res);
 }
 private static class Window{final long start;final int count;Window(long s){this(s,1);}Window(long s,int c){start=s;count=c;}Window increment(){return new Window(start,count+1);}}
}
