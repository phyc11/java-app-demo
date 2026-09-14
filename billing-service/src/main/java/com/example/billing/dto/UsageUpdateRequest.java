package com.example.billing.dto;
public class UsageUpdateRequest {
    private Long workspaceId; private String resourceType; private Long delta;
    public Long getWorkspaceId(){return workspaceId;} public void setWorkspaceId(Long v){workspaceId=v;}
    public String getResourceType(){return resourceType;} public void setResourceType(String v){resourceType=v;}
    public Long getDelta(){return delta;} public void setDelta(Long v){delta=v;}
}
