package com.example.timetracking.model;

import javax.persistence.*;

@Entity
@Table(name = "task_estimates")
public class TaskEstimate {

    @Id
    private Long taskId;

    private Long projectId;

    @Column(nullable = false)
    private Double estimatedHours;

    public TaskEstimate() {}

    public TaskEstimate(Long taskId, Long projectId, Double estimatedHours) {
        this.taskId = taskId;
        this.projectId = projectId;
        this.estimatedHours = estimatedHours;
    }

    public Long getTaskId() { return taskId; }
    public void setTaskId(Long taskId) { this.taskId = taskId; }

    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }

    public Double getEstimatedHours() { return estimatedHours; }
    public void setEstimatedHours(Double estimatedHours) { this.estimatedHours = estimatedHours; }
}
