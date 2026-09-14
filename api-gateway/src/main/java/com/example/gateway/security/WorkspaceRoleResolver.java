package com.example.gateway.security;
import com.fasterxml.jackson.databind.*; import org.springframework.beans.factory.annotation.Value; import org.springframework.stereotype.Component; import org.springframework.web.client.RestTemplate;
@Component public class WorkspaceRoleResolver {private final RestTemplate http;private final ObjectMapper json;
 @Value("${workspace-service.url:http://localhost:8092}")String url;
 public WorkspaceRoleResolver(RestTemplate h,ObjectMapper j){http=h;json=j;}
 public String resolve(String workspaceId,String username){if(workspaceId==null||username==null)return null;try{String body=http.getForObject(url+"/api/workspaces/"+Long.parseLong(workspaceId),String.class);JsonNode members=json.readTree(body).path("data").path("members");for(JsonNode m:members)if(username.equalsIgnoreCase(m.path("username").asText()))return m.path("role").asText();return null;}catch(Exception e){throw new IllegalArgumentException("Cannot verify workspace membership");}}
}
