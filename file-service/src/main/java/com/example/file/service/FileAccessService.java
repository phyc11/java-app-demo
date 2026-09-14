package com.example.file.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class FileAccessService {
    private final RestTemplate http = new RestTemplate();
    private final ObjectMapper json = new ObjectMapper();
    private final String taskServiceUrl;

    public FileAccessService(@Value("${services.task-url:http://localhost:8082}") String taskServiceUrl) {
        this.taskServiceUrl = taskServiceUrl;
    }

    public void assertTaskInWorkspace(Long taskId, Long workspaceId) {
        if (taskId == null) throw new IllegalArgumentException("entityId is required for TASK files");
        try {
            String body = http.getForObject(taskServiceUrl + "/api/tasks/" + taskId, String.class);
            JsonNode data = json.readTree(body).path("data");
            if (!workspaceId.equals(data.path("workspaceId").asLong()))
                throw new SecurityException("Task does not belong to this workspace");
        } catch (SecurityException e) { throw e; }
        catch (Exception e) { throw new IllegalArgumentException("Cannot verify task access", e); }
    }

    public boolean taskExists(Long taskId, Long workspaceId) {
        try { assertTaskInWorkspace(taskId, workspaceId); return true; }
        catch (RuntimeException e) { return false; }
    }
}
