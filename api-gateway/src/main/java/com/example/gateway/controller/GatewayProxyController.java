package com.example.gateway.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.Collections;

@RestController
public class GatewayProxyController {

    private final RestTemplate restTemplate;

    @Value("${auth-service.url:http://localhost:8081}")
    private String authServiceUrl;

    @Value("${task-service.url:http://localhost:8082}")
    private String taskServiceUrl;

    @Value("${analytics-service.url:http://localhost:8084}")
    private String analyticsServiceUrl;

    @Value("${notification-service.url:http://localhost:8085}")
    private String notificationServiceUrl;

    @Value("${search-service.url:http://localhost:8086}")
    private String searchServiceUrl;

    @Value("${file-service.url:http://localhost:8088}")
    private String fileServiceUrl;

    @Value("${time-tracking-service.url:http://localhost:8089}")
    private String timeTrackingServiceUrl;

    @Value("${project-service.url:http://localhost:8090}")
    private String projectServiceUrl;

    @Value("${billing-service.url:http://localhost:8091}")
    private String billingServiceUrl;

    @Value("${comment-service.url:http://localhost:8087}")
    private String commentServiceUrl;

    @Value("${workspace-service.url:http://localhost:8092}")
    private String workspaceServiceUrl;

    public GatewayProxyController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @RequestMapping({"/api/auth/**", "/api/users/**", "/api/tasks/**", "/api/categories/**", "/api/analytics/**", "/api/export/**", "/api/notifications/**", "/api/search/**", "/api/files/**", "/api/time-tracking/**", "/api/projects/**", "/api/billing/**", "/api/comments/**", "/api/workspaces/**"})
    public ResponseEntity<byte[]> proxyRequest(@RequestBody(required = false) byte[] body,
                                              HttpMethod method,
                                              HttpServletRequest request) {
        String requestPath = request.getRequestURI();
        String queryString = request.getQueryString();
        if (queryString != null) {
            requestPath += "?" + queryString;
        }

        String targetBaseUrl;
        if (requestPath.startsWith("/api/auth") || requestPath.startsWith("/api/users")) {
            targetBaseUrl = authServiceUrl;
        } else if (requestPath.startsWith("/api/tasks") || requestPath.startsWith("/api/categories")) {
            targetBaseUrl = taskServiceUrl;
        } else if (requestPath.startsWith("/api/notifications")) {
            targetBaseUrl = notificationServiceUrl;
        } else if (requestPath.startsWith("/api/search")) {
            targetBaseUrl = searchServiceUrl;
        } else if (requestPath.startsWith("/api/files")) {
            targetBaseUrl = fileServiceUrl;
        } else if (requestPath.startsWith("/api/time-tracking")) {
            targetBaseUrl = timeTrackingServiceUrl;
        } else if (requestPath.startsWith("/api/projects")) {
            targetBaseUrl = projectServiceUrl;
        } else if (requestPath.startsWith("/api/billing")) {
            targetBaseUrl = billingServiceUrl;
        } else if (requestPath.startsWith("/api/comments")) {
            targetBaseUrl = commentServiceUrl;
        } else if (requestPath.startsWith("/api/workspaces")) {
            targetBaseUrl = workspaceServiceUrl;
        } else {
            targetBaseUrl = analyticsServiceUrl;
        }

        String targetUrl = targetBaseUrl + requestPath;

        HttpHeaders headers = new HttpHeaders();
        Collections.list(request.getHeaderNames()).forEach(headerName -> {
            if (!headerName.equalsIgnoreCase("X-User") && !headerName.equalsIgnoreCase("X-Role") && !headerName.equalsIgnoreCase("X-Workspace-Role") && !headerName.equalsIgnoreCase("Host") && !headerName.equalsIgnoreCase("Content-Length")) headers.add(headerName, request.getHeader(headerName));
        });
        String trustedUser=(String)request.getAttribute("trustedUser"); String trustedRole=(String)request.getAttribute("trustedRole");
        if(trustedUser!=null){headers.set("X-User",trustedUser);headers.set("X-Role",trustedRole);String workspaceRole=(String)request.getAttribute("trustedWorkspaceRole");if(workspaceRole!=null)headers.set("X-Workspace-Role",workspaceRole);}

        HttpEntity<byte[]> httpEntity = new HttpEntity<>(body, headers);

        try {
            return restTemplate.exchange(URI.create(targetUrl), method, httpEntity, byte[].class);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body(("{\"success\":false,\"message\":\"Service Unavailable: " + ex.getMessage() + "\"}").getBytes());
        }
    }
}
