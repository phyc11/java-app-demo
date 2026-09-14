package com.example.timetracking.dto;

public class TaskTimeSummaryDto {
    private Long taskId;
    private Double estimatedHours;
    private Double totalLoggedHours;
    private Double remainingHours;
    private Double progressPercentage;

    public TaskTimeSummaryDto() {}

    public TaskTimeSummaryDto(Long taskId, Double estimatedHours, Double totalLoggedHours) {
        this.taskId = taskId;
        this.estimatedHours = estimatedHours != null ? estimatedHours : 0.0;
        this.totalLoggedHours = totalLoggedHours != null ? totalLoggedHours : 0.0;
        this.remainingHours = Math.max(0.0, this.estimatedHours - this.totalLoggedHours);
        if (this.estimatedHours > 0) {
            this.progressPercentage = Math.min(100.0, (this.totalLoggedHours / this.estimatedHours) * 100.0);
        } else {
            this.progressPercentage = 0.0;
        }
    }

    public Long getTaskId() { return taskId; }
    public void setTaskId(Long taskId) { this.taskId = taskId; }

    public Double getEstimatedHours() { return estimatedHours; }
    public void setEstimatedHours(Double estimatedHours) { this.estimatedHours = estimatedHours; }

    public Double getTotalLoggedHours() { return totalLoggedHours; }
    public void setTotalLoggedHours(Double totalLoggedHours) { this.totalLoggedHours = totalLoggedHours; }

    public Double getRemainingHours() { return remainingHours; }
    public void setRemainingHours(Double remainingHours) { this.remainingHours = remainingHours; }

    public Double getProgressPercentage() { return progressPercentage; }
    public void setProgressPercentage(Double progressPercentage) { this.progressPercentage = progressPercentage; }
}
