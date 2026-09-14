package com.example.timetracking.model;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "worklogs")
public class Worklog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long taskId;

    private Long projectId;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private Double timeSpentHours;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private LocalDate logDate;

    private LocalDateTime createdAt;

    public Worklog() {
        this.createdAt = LocalDateTime.now();
        this.logDate = LocalDate.now();
    }

    public Worklog(Long taskId, Long projectId, String username, Double timeSpentHours, String description, LocalDate logDate) {
        this.taskId = taskId;
        this.projectId = projectId;
        this.username = username;
        this.timeSpentHours = timeSpentHours;
        this.description = description;
        this.logDate = logDate != null ? logDate : LocalDate.now();
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
