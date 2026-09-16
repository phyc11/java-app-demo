package com.example.task.dto;

import com.example.task.model.Priority;
import com.example.task.model.Status;
import com.example.task.model.Task;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

public class TaskDTO {
    private Long id;
    private Long version;
    private Long workspaceId;
    private Long projectId;
    private Long parentTaskId;
    private String assignee;
    private Set<String> watchers = new LinkedHashSet<>();
    private Set<Long> dependencyIds = new LinkedHashSet<>();

    @NotBlank(message = "Task title is required")
    @Size(max = 255, message = "Task title must not exceed 255 characters")
    private String title;

    @Size(max = 2000, message = "Task description must not exceed 2000 characters")
    private String description;

    private Status status;
    private Priority priority;
    private Long categoryId;
    private String categoryName;
    private String categoryColor;
    private String createdBy;
    private Integer position;
    private LocalDateTime dueDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean archived;
    private LocalDateTime archivedAt;
    private String archivedBy;
    private com.example.task.model.RecurrenceRule recurrenceRule;

    public TaskDTO() {}

    public TaskDTO(Task task) {
        this.id = task.getId();
        this.version = task.getVersion();
        this.workspaceId = task.getWorkspaceId();
        this.projectId = task.getProjectId();
        this.parentTaskId = task.getParentTaskId();
        this.assignee = task.getAssignee();
        this.watchers = new LinkedHashSet<>(task.getWatchers());
        this.title = task.getTitle();
        this.description = task.getDescription();
        this.status = task.getStatus();
        this.priority = task.getPriority();
        if (task.getCategory() != null) {
            this.categoryId = task.getCategory().getId();
            this.categoryName = task.getCategory().getName();
            this.categoryColor = task.getCategory().getColor();
        }
        this.createdBy = task.getCreatedBy();
        this.position = task.getPosition();
        this.dueDate = task.getDueDate();
        this.createdAt = task.getCreatedAt();
        this.updatedAt = task.getUpdatedAt();
        this.archived=task.isArchived();this.archivedAt=task.getArchivedAt();this.archivedBy=task.getArchivedBy();this.recurrenceRule=task.getRecurrenceRule();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }
    public Long getWorkspaceId() { return workspaceId; }
    public void setWorkspaceId(Long workspaceId) { this.workspaceId = workspaceId; }
    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }
    public Long getParentTaskId() { return parentTaskId; }
    public void setParentTaskId(Long parentTaskId) { this.parentTaskId = parentTaskId; }
    public String getAssignee() { return assignee; }
    public void setAssignee(String assignee) { this.assignee = assignee; }
    public Set<String> getWatchers() { return watchers; }
    public void setWatchers(Set<String> watchers) { this.watchers = watchers; }
    public Set<Long> getDependencyIds() { return dependencyIds; }
    public void setDependencyIds(Set<Long> dependencyIds) { this.dependencyIds = dependencyIds; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public Priority getPriority() { return priority; }
    public void setPriority(Priority priority) { this.priority = priority; }

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public String getCategoryColor() { return categoryColor; }
    public void setCategoryColor(String categoryColor) { this.categoryColor = categoryColor; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public Integer getPosition() { return position; }
    public void setPosition(Integer position) { this.position = position; }

    public LocalDateTime getDueDate() { return dueDate; }
    public void setDueDate(LocalDateTime dueDate) { this.dueDate = dueDate; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public boolean isArchived(){return archived;} public void setArchived(boolean v){archived=v;} public LocalDateTime getArchivedAt(){return archivedAt;} public void setArchivedAt(LocalDateTime v){archivedAt=v;} public String getArchivedBy(){return archivedBy;} public void setArchivedBy(String v){archivedBy=v;}
    public com.example.task.model.RecurrenceRule getRecurrenceRule(){return recurrenceRule;} public void setRecurrenceRule(com.example.task.model.RecurrenceRule v){recurrenceRule=v;}
}
