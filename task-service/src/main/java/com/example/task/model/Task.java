package com.example.task.model;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private Long version;

    @Column(nullable = false)
    private Long workspaceId;

    private Long projectId;
    private Long parentTaskId;
    private String assignee;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "task_watchers", joinColumns = @JoinColumn(name = "task_id"))
    @Column(name = "username", nullable = false)
    private Set<String> watchers = new LinkedHashSet<>();

    @Column(nullable = false)
    private String title;

    @Column(length = 2000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.TODO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Priority priority = Priority.MEDIUM;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id")
    private Category category;

    private String createdBy;

    private Integer position = 0;

    private LocalDateTime dueDate;
    @Column(nullable=false) private boolean archived=false;
    private LocalDateTime archivedAt;
    private String archivedBy;
    @Enumerated(EnumType.STRING) private RecurrenceRule recurrenceRule;
    @Column(nullable=false) private boolean recurrenceGenerated=false;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Task() {}

    public Task(String title, String description, Status status, Priority priority, Category category, String createdBy) {
        this.title = title;
        this.description = description;
        this.status = status != null ? status : Status.TODO;
        this.priority = priority != null ? priority : Priority.MEDIUM;
        this.category = category;
        this.createdBy = createdBy;
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
    public void setWatchers(Set<String> watchers) { this.watchers = watchers == null ? new LinkedHashSet<>() : watchers; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public Priority getPriority() { return priority; }
    public void setPriority(Priority priority) { this.priority = priority; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public Integer getPosition() { return position; }
    public void setPosition(Integer position) { this.position = position; }

    public LocalDateTime getDueDate() { return dueDate; }
    public void setDueDate(LocalDateTime dueDate) { this.dueDate = dueDate; }
    public boolean isArchived(){return archived;} public void setArchived(boolean v){archived=v;}
    public LocalDateTime getArchivedAt(){return archivedAt;} public void setArchivedAt(LocalDateTime v){archivedAt=v;}
    public String getArchivedBy(){return archivedBy;} public void setArchivedBy(String v){archivedBy=v;}
    public RecurrenceRule getRecurrenceRule(){return recurrenceRule;} public void setRecurrenceRule(RecurrenceRule v){recurrenceRule=v;}
    public boolean isRecurrenceGenerated(){return recurrenceGenerated;} public void setRecurrenceGenerated(boolean v){recurrenceGenerated=v;}

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
