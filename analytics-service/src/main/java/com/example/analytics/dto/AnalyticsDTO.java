package com.example.analytics.dto;

import java.util.Map;

public class AnalyticsDTO {
    private long totalTasks;
    private long completedTasks;
    private double completionRate;
    private Map<String, Long> categoryDistribution;
    private Map<String, Long> statusDistribution;
    private Map<String, Long> priorityDistribution;

    public AnalyticsDTO() {}

    public AnalyticsDTO(long totalTasks, long completedTasks, double completionRate,
                        Map<String, Long> categoryDistribution,
                        Map<String, Long> statusDistribution,
                        Map<String, Long> priorityDistribution) {
        this.totalTasks = totalTasks;
        this.completedTasks = completedTasks;
        this.completionRate = completionRate;
        this.categoryDistribution = categoryDistribution;
        this.statusDistribution = statusDistribution;
        this.priorityDistribution = priorityDistribution;
    }

    public long getTotalTasks() { return totalTasks; }
    public void setTotalTasks(long totalTasks) { this.totalTasks = totalTasks; }

    public long getCompletedTasks() { return completedTasks; }
    public void setCompletedTasks(long completedTasks) { this.completedTasks = completedTasks; }

    public double getCompletionRate() { return completionRate; }
    public void setCompletionRate(double completionRate) { this.completionRate = completionRate; }

    public Map<String, Long> getCategoryDistribution() { return categoryDistribution; }
    public void setCategoryDistribution(Map<String, Long> categoryDistribution) { this.categoryDistribution = categoryDistribution; }

    public Map<String, Long> getStatusDistribution() { return statusDistribution; }
    public void setStatusDistribution(Map<String, Long> statusDistribution) { this.statusDistribution = statusDistribution; }

    public Map<String, Long> getPriorityDistribution() { return priorityDistribution; }
    public void setPriorityDistribution(Map<String, Long> priorityDistribution) { this.priorityDistribution = priorityDistribution; }
}
