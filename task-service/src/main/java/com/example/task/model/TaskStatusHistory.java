package com.example.task.model;
import javax.persistence.*; import java.time.LocalDateTime;
@Entity @Table(name="task_status_history")
public class TaskStatusHistory {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false) private Long taskId; @Enumerated(EnumType.STRING) private Status fromStatus;
 @Enumerated(EnumType.STRING) @Column(nullable=false) private Status toStatus;
 private String changedBy; @Column(nullable=false) private LocalDateTime changedAt;
 public TaskStatusHistory(){} public TaskStatusHistory(Long taskId,Status from,Status to,String by){this.taskId=taskId;fromStatus=from;toStatus=to;changedBy=by;changedAt=LocalDateTime.now();}
 public Long getId(){return id;} public Long getTaskId(){return taskId;} public Status getFromStatus(){return fromStatus;}
 public Status getToStatus(){return toStatus;} public String getChangedBy(){return changedBy;} public LocalDateTime getChangedAt(){return changedAt;}
}
