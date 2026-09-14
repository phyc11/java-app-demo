package com.example.task.model;
import javax.persistence.*;
@Entity @Table(name="task_dependencies",uniqueConstraints=@UniqueConstraint(columnNames={"taskId","dependsOnTaskId"}))
public class TaskDependency {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false) private Long taskId; @Column(nullable=false) private Long dependsOnTaskId;
 public TaskDependency(){} public TaskDependency(Long taskId,Long dependsOnTaskId){this.taskId=taskId;this.dependsOnTaskId=dependsOnTaskId;}
 public Long getId(){return id;} public Long getTaskId(){return taskId;} public Long getDependsOnTaskId(){return dependsOnTaskId;}
}
