package com.example.timetracking.dto;

import java.time.LocalDate;

public class WorklogRequestDto {
    private Long taskId;
    private Long projectId;
    private String username;
    private Double timeSpentHours;
    private String description;
    private LocalDate logDate;

    public WorklogRequestDto() {}

    public Long getTaskId() { return taskId; }
    public void setTaskId(Long taskId) { this.taskId = taskId; }

    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public Double getTimeSpentHours() { return timeSpentHours; }
    public void setTimeSpentHours(Double timeSpentHours) { this.timeSpentHours = timeSpentHours; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getLogDate() { return logDate; }
    public void setLogDate(LocalDate logDate) { this.logDate = logDate; }
}
