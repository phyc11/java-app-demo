package com.example.billing.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "subscription_history")
public class SubscriptionHistory {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false) private Long workspaceId;
    private String previousPlan;
    @Column(nullable = false) private String newPlan;
    @Column(nullable = false) private String action;
    private String invoiceNumber;
    @Column(nullable = false) private LocalDateTime changedAt;
    public SubscriptionHistory() {}
    public SubscriptionHistory(Long workspaceId, String previousPlan, String newPlan, String action, String invoiceNumber) {
        this.workspaceId=workspaceId; this.previousPlan=previousPlan; this.newPlan=newPlan;
        this.action=action; this.invoiceNumber=invoiceNumber; this.changedAt=LocalDateTime.now();
    }
    public Long getId(){return id;} public Long getWorkspaceId(){return workspaceId;}
    public String getPreviousPlan(){return previousPlan;} public String getNewPlan(){return newPlan;}
    public String getAction(){return action;} public String getInvoiceNumber(){return invoiceNumber;}
    public LocalDateTime getChangedAt(){return changedAt;}
}
