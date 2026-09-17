package com.example.task.dto;

public class TaskMoveRequest {
    private Long version;
    private Long projectId;
    private Long parentTaskId;
    private Integer position;

    public Long getVersion(){return version;} public void setVersion(Long value){version=value;}
    public Long getProjectId(){return projectId;} public void setProjectId(Long value){projectId=value;}
    public Long getParentTaskId(){return parentTaskId;} public void setParentTaskId(Long value){parentTaskId=value;}
    public Integer getPosition(){return position;} public void setPosition(Integer value){position=value;}
}
