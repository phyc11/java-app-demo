package com.example.project.model;
import javax.persistence.*; import java.time.LocalDateTime;
@Entity @Table(name="project_templates",uniqueConstraints=@UniqueConstraint(columnNames={"workspaceId","name"}))
public class ProjectTemplate {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id; @Column(nullable=false) private Long workspaceId;
 @Column(nullable=false) private String name; @Column(length=1000) private String description; @Lob private String tagsJson="[]"; @Lob private String milestonesJson="[]";
 @Column(nullable=false) private String createdBy; @Column(nullable=false) private LocalDateTime createdAt=LocalDateTime.now();
 public ProjectTemplate(){} public Long getId(){return id;} public Long getWorkspaceId(){return workspaceId;} public void setWorkspaceId(Long v){workspaceId=v;}
 public String getName(){return name;} public void setName(String v){name=v;} public String getDescription(){return description;} public void setDescription(String v){description=v;}
 public String getTagsJson(){return tagsJson;} public void setTagsJson(String v){tagsJson=v;} public String getMilestonesJson(){return milestonesJson;} public void setMilestonesJson(String v){milestonesJson=v;}
 public String getCreatedBy(){return createdBy;} public void setCreatedBy(String v){createdBy=v;} public LocalDateTime getCreatedAt(){return createdAt;}
}
