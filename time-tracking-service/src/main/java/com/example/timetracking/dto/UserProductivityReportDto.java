package com.example.timetracking.dto;

public class UserProductivityReportDto {
    private String username;
    private Double totalLoggedHours;
    private Long tasksWorkedOnCount;
    private Long totalLogEntries;

    public UserProductivityReportDto() {}

    public UserProductivityReportDto(String username, Double totalLoggedHours, Long tasksWorkedOnCount, Long totalLogEntries) {
        this.username = username;
        this.totalLoggedHours = totalLoggedHours != null ? totalLoggedHours : 0.0;
        this.tasksWorkedOnCount = tasksWorkedOnCount != null ? tasksWorkedOnCount : 0L;
        this.totalLogEntries = totalLogEntries != null ? totalLogEntries : 0L;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public Double getTotalLoggedHours() { return totalLoggedHours; }
    public void setTotalLoggedHours(Double totalLoggedHours) { this.totalLoggedHours = totalLoggedHours; }

    public Long getTasksWorkedOnCount() { return tasksWorkedOnCount; }
    public void setTasksWorkedOnCount(Long tasksWorkedOnCount) { this.tasksWorkedOnCount = tasksWorkedOnCount; }

    public Long getTotalLogEntries() { return totalLogEntries; }
    public void setTotalLogEntries(Long totalLogEntries) { this.totalLogEntries = totalLogEntries; }
}
