package com.example.billing.dto;

public class SubscribeRequestDto {
    private Long workspaceId;
    private String ownerUsername;
    private String planName; // FREE, PRO, ENTERPRISE
    private String paymentGateway; // STRIPE, VNPAY, MOMO
    private String currency; // USD, VND

    public SubscribeRequestDto() {}

    public Long getWorkspaceId() { return workspaceId; }
    public void setWorkspaceId(Long workspaceId) { this.workspaceId = workspaceId; }

    public String getOwnerUsername() { return ownerUsername; }
    public void setOwnerUsername(String ownerUsername) { this.ownerUsername = ownerUsername; }

    public String getPlanName() { return planName; }
    public void setPlanName(String planName) { this.planName = planName; }

    public String getPaymentGateway() { return paymentGateway; }
    public void setPaymentGateway(String paymentGateway) { this.paymentGateway = paymentGateway; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
}
