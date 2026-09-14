package com.example.billing.model;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "workspace_subscriptions")
public class WorkspaceSubscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long workspaceId;

    @Column(nullable = false)
    private String ownerUsername;

    @Column(nullable = false)
    private String planName; // FREE, PRO, ENTERPRISE

    private String status; // ACTIVE, CANCELLED, EXPIRED
    private LocalDate startDate;
    private LocalDate endDate;

    public WorkspaceSubscription() {
        this.status = "ACTIVE";
        this.startDate = LocalDate.now();
        this.endDate = LocalDate.now().plusYears(100); // Default lifetime for FREE
    }

    public WorkspaceSubscription(Long workspaceId, String ownerUsername, String planName, LocalDate startDate, LocalDate endDate) {
        this.workspaceId = workspaceId;
        this.ownerUsername = ownerUsername;
        this.planName = planName != null ? planName.toUpperCase() : "FREE";
        this.status = "ACTIVE";
        this.startDate = startDate != null ? startDate : LocalDate.now();
        this.endDate = endDate;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getWorkspaceId() { return workspaceId; }
    public void setWorkspaceId(Long workspaceId) { this.workspaceId = workspaceId; }

    public String getOwnerUsername() { return ownerUsername; }
    public void setOwnerUsername(String ownerUsername) { this.ownerUsername = ownerUsername; }

    public String getPlanName() { return planName; }
    public void setPlanName(String planName) { this.planName = planName != null ? planName.toUpperCase() : "FREE"; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
}
