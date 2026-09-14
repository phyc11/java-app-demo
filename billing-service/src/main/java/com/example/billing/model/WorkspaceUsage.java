package com.example.billing.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="workspace_usage", uniqueConstraints=@UniqueConstraint(columnNames={"workspaceId","resourceType"}))
public class WorkspaceUsage {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false) private Long workspaceId;
    @Column(nullable=false, length=30) private String resourceType;
    @Column(nullable=false) private Long usageValue;
    @Column(nullable=false) private LocalDateTime updatedAt;
    public WorkspaceUsage() {}
    public WorkspaceUsage(Long workspaceId,String resourceType){this.workspaceId=workspaceId;this.resourceType=resourceType;this.usageValue=0L;this.updatedAt=LocalDateTime.now();}
    public Long getId(){return id;} public Long getWorkspaceId(){return workspaceId;}
    public String getResourceType(){return resourceType;} public Long getUsageValue(){return usageValue;}
    public void setUsageValue(Long value){this.usageValue=value;this.updatedAt=LocalDateTime.now();}
    public LocalDateTime getUpdatedAt(){return updatedAt;}
}
