package com.example.analytics.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="analytics_snapshots", indexes={@Index(name="idx_snapshot_scope",columnList="workspaceId,projectId,capturedAt")})
public class AnalyticsSnapshot {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false) private Long workspaceId; private Long projectId;
 private long totalTasks,completedTasks,overdueTasks; private double completionRate,averageCycleTimeHours,averageLeadTimeHours;
 @Lob @Column(nullable=false) private String statusJson="{}"; @Lob @Column(nullable=false) private String workloadJson="{}";
 @Column(nullable=false) private LocalDateTime capturedAt=LocalDateTime.now();
 public AnalyticsSnapshot(){}
 public Long getId(){return id;} public Long getWorkspaceId(){return workspaceId;} public void setWorkspaceId(Long v){workspaceId=v;}
 public Long getProjectId(){return projectId;} public void setProjectId(Long v){projectId=v;} public long getTotalTasks(){return totalTasks;} public void setTotalTasks(long v){totalTasks=v;}
 public long getCompletedTasks(){return completedTasks;} public void setCompletedTasks(long v){completedTasks=v;} public long getOverdueTasks(){return overdueTasks;} public void setOverdueTasks(long v){overdueTasks=v;}
 public double getCompletionRate(){return completionRate;} public void setCompletionRate(double v){completionRate=v;} public double getAverageCycleTimeHours(){return averageCycleTimeHours;} public void setAverageCycleTimeHours(double v){averageCycleTimeHours=v;}
 public double getAverageLeadTimeHours(){return averageLeadTimeHours;} public void setAverageLeadTimeHours(double v){averageLeadTimeHours=v;} public String getStatusJson(){return statusJson;} public void setStatusJson(String v){statusJson=v;}
 public String getWorkloadJson(){return workloadJson;} public void setWorkloadJson(String v){workloadJson=v;} public LocalDateTime getCapturedAt(){return capturedAt;} public void setCapturedAt(LocalDateTime v){capturedAt=v;}
}
