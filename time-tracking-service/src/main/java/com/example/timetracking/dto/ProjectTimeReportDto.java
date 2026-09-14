package com.example.timetracking.dto;

public class ProjectTimeReportDto {
    private Long projectId;
    private Double totalEstimatedHours;
    private Double totalLoggedHours;
    private Long trackedTasksCount;

    public ProjectTimeReportDto() {}

    public ProjectTimeReportDto(Long projectId, Double totalEstimatedHours, Double totalLoggedHours, Long trackedTasksCount) {
        this.projectId = projectId;
        this.totalEstimatedHours = totalEstimatedHours != null ? totalEstimatedHours : 0.0;
        this.totalLoggedHours = totalLoggedHours != null ? totalLoggedHours : 0.0;
        this.trackedTasksCount = trackedTasksCount != null ? trackedTasksCount : 0L;
    }

    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }

    public Double getTotalEstimatedHours() { return totalEstimatedHours; }
    public void setTotalEstimatedHours(Double totalEstimatedHours) { this.totalEstimatedHours = totalEstimatedHours; }

    public Double getTotalLoggedHours() { return totalLoggedHours; }
    public void setTotalLoggedHours(Double totalLoggedHours) { this.totalLoggedHours = totalLoggedHours; }

    public Long getTrackedTasksCount() { return trackedTasksCount; }
    public void setTrackedTasksCount(Long trackedTasksCount) { this.trackedTasksCount = trackedTasksCount; }
}
