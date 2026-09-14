package com.example.gateway.security;
import io.jsonwebtoken.*; import io.jsonwebtoken.security.Keys; import org.springframework.beans.factory.annotation.Value; import org.springframework.stereotype.Component; import org.springframework.util.StringUtils; import org.springframework.web.filter.OncePerRequestFilter;
import javax.annotation.PostConstruct; import javax.crypto.SecretKey; import javax.servlet.*; import javax.servlet.http.*; import java.io.IOException;
@Component public class GatewayAuthFilter extends OncePerRequestFilter {
 @Value("${jwt.secret}") private String secret; private SecretKey key;
 private final WorkspaceRoleResolver roles; public GatewayAuthFilter(WorkspaceRoleResolver roles){this.roles=roles;}
 @PostConstruct void init(){key=Keys.hmacShaKeyFor(secret.getBytes(java.nio.charset.StandardCharsets.UTF_8));}
 @Override protected boolean shouldNotFilter(HttpServletRequest r){String p=r.getRequestURI();if(!p.startsWith("/api/"))return true;return p.equals("/api/auth/login")||p.equals("/api/auth/register")||p.equals("/api/auth/refresh")||p.equals("/api/auth/verify-email")||p.equals("/api/auth/forgot-password")||p.equals("/api/auth/reset-password")||p.equals("/api/billing/stripe/webhook");}
 @Override protected void doFilterInternal(HttpServletRequest req,HttpServletResponse res,FilterChain chain)throws ServletException,IOException{
  String h=req.getHeader("Authorization");if(!StringUtils.hasText(h)||!h.startsWith("Bearer ")){unauthorized(res);return;}
  try{Claims c=Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(h.substring(7)).getBody();req.setAttribute("trustedUser",c.getSubject());req.setAttribute("trustedRole",c.get("role",String.class));if(isWorkspaceScoped(req.getRequestURI())){String wid=req.getHeader("X-Workspace-Id");if(!StringUtils.hasText(wid)){error(res,400,"X-Workspace-Id is required");return;}String role=roles.resolve(wid,c.getSubject());if(role==null){error(res,403,"Workspace membership required");return;}if(!"GET".equals(req.getMethod())&&"VIEWER".equals(role)){error(res,403,"Viewer cannot modify workspace data");return;}if(isAdministration(req.getRequestURI())&&!("OWNER".equals(role)||"ADMIN".equals(role))){error(res,403,"Owner or admin role required");return;}req.setAttribute("trustedWorkspaceRole",role);}chain.doFilter(req,res);}catch(JwtException|IllegalArgumentException e){unauthorized(res);}
 }
 private void unauthorized(HttpServletResponse r)throws IOException{r.setStatus(401);r.setContentType("application/json");r.getWriter().write("{\"success\":false,\"message\":\"Invalid or missing access token\"}");}
 private boolean isWorkspaceScoped(String p){return p.startsWith("/api/tasks")||p.startsWith("/api/projects")||p.startsWith("/api/comments")||p.startsWith("/api/files")||p.startsWith("/api/time-tracking")||p.startsWith("/api/workspaces/")||p.startsWith("/api/billing/subscription")||p.startsWith("/api/billing/usage")||p.startsWith("/api/billing/invoices");}
 private boolean isAdministration(String p){return p.contains("/members")||p.contains("/invitations")||p.contains("/owner/");}
 private void error(HttpServletResponse r,int status,String message)throws IOException{r.setStatus(status);r.setContentType("application/json");r.getWriter().write("{\"success\":false,\"message\":\""+message+"\"}");}
}
