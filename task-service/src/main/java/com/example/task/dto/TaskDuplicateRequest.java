package com.example.task.dto;

import java.time.LocalDateTime;

public class TaskDuplicateRequest {
    private String title;
    private Long projectId;
    private Long parentTaskId;
    private LocalDateTime dueDate;
    private boolean copyWatchers;
    private boolean copyDependencies;

    public String getTitle(){return title;} public void setTitle(String value){title=value;}
    public Long getProjectId(){return projectId;} public void setProjectId(Long value){projectId=value;}
    public Long getParentTaskId(){return parentTaskId;} public void setParentTaskId(Long value){parentTaskId=value;}
    public LocalDateTime getDueDate(){return dueDate;} public void setDueDate(LocalDateTime value){dueDate=value;}
    public boolean isCopyWatchers(){return copyWatchers;} public void setCopyWatchers(boolean value){copyWatchers=value;}
    public boolean isCopyDependencies(){return copyDependencies;} public void setCopyDependencies(boolean value){copyDependencies=value;}
}
