package com.example.billing.dto;

public class QuotaStatusDto {
    private String planName;
    private boolean allowed;
    private String reason;
    private String resourceType;
    private Long currentUsage;
    private Long maxLimit;

    public QuotaStatusDto() {}

    public QuotaStatusDto(String planName, boolean allowed, String reason, String resourceType, Long currentUsage, Long maxLimit) {
        this.planName = planName;
        this.allowed = allowed;
        this.reason = reason;
        this.resourceType = resourceType;
        this.currentUsage = currentUsage;
        this.maxLimit = maxLimit;
    }

    public String getPlanName() { return planName; }
    public void setPlanName(String planName) { this.planName = planName; }

    public boolean isAllowed() { return allowed; }
    public void setAllowed(boolean allowed) { this.allowed = allowed; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getResourceType() { return resourceType; }
    public void setResourceType(String resourceType) { this.resourceType = resourceType; }

    public Long getCurrentUsage() { return currentUsage; }
    public void setCurrentUsage(Long currentUsage) { this.currentUsage = currentUsage; }

    public Long getMaxLimit() { return maxLimit; }
    public void setMaxLimit(Long maxLimit) { this.maxLimit = maxLimit; }
}
