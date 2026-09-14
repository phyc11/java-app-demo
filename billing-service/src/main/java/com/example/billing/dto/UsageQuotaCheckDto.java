package com.example.billing.dto;

public class UsageQuotaCheckDto {
    private Long workspaceId;
    private Integer currentProjectCount;
    private Integer currentMemberCount;
    private Long currentStorageMb;

    public UsageQuotaCheckDto() {}

    public Long getWorkspaceId() { return workspaceId; }
    public void setWorkspaceId(Long workspaceId) { this.workspaceId = workspaceId; }

    public Integer getCurrentProjectCount() { return currentProjectCount; }
    public void setCurrentProjectCount(Integer currentProjectCount) { this.currentProjectCount = currentProjectCount; }

    public Integer getCurrentMemberCount() { return currentMemberCount; }
    public void setCurrentMemberCount(Integer currentMemberCount) { this.currentMemberCount = currentMemberCount; }

    public Long getCurrentStorageMb() { return currentStorageMb; }
    public void setCurrentStorageMb(Long currentStorageMb) { this.currentStorageMb = currentStorageMb; }
}
