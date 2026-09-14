package com.example.analytics.dto;
import java.time.LocalDateTime; import java.util.LinkedHashMap; import java.util.Map;
public class AnalyticsDTO {
 private Long workspaceId,projectId; private long totalTasks,completedTasks,overdueTasks; private double completionRate,averageCycleTimeHours,averageLeadTimeHours;
 private Map<String,Long> statusDistribution=new LinkedHashMap<>(),workloadByAssignee=new LinkedHashMap<>(); private LocalDateTime capturedAt;
 public AnalyticsDTO(){}
 public Long getWorkspaceId(){return workspaceId;} public void setWorkspaceId(Long v){workspaceId=v;} public Long getProjectId(){return projectId;} public void setProjectId(Long v){projectId=v;}
 public long getTotalTasks(){return totalTasks;} public void setTotalTasks(long v){totalTasks=v;} public long getCompletedTasks(){return completedTasks;} public void setCompletedTasks(long v){completedTasks=v;}
 public long getOverdueTasks(){return overdueTasks;} public void setOverdueTasks(long v){overdueTasks=v;} public double getCompletionRate(){return completionRate;} public void setCompletionRate(double v){completionRate=v;}
 public double getAverageCycleTimeHours(){return averageCycleTimeHours;} public void setAverageCycleTimeHours(double v){averageCycleTimeHours=v;} public double getAverageLeadTimeHours(){return averageLeadTimeHours;} public void setAverageLeadTimeHours(double v){averageLeadTimeHours=v;}
 public Map<String,Long> getStatusDistribution(){return statusDistribution;} public void setStatusDistribution(Map<String,Long> v){statusDistribution=v;} public Map<String,Long> getWorkloadByAssignee(){return workloadByAssignee;} public void setWorkloadByAssignee(Map<String,Long> v){workloadByAssignee=v;}
 public LocalDateTime getCapturedAt(){return capturedAt;} public void setCapturedAt(LocalDateTime v){capturedAt=v;}
}
