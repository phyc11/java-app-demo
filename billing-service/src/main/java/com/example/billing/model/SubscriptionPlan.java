package com.example.billing.model;

import javax.persistence.*;

@Entity
@Table(name = "subscription_plans")
public class SubscriptionPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String planName; // FREE, PRO, ENTERPRISE

    @Column(nullable = false)
    private Double monthlyPrice;

    private Integer maxProjects; // -1 for unlimited
    private Integer maxMembersPerProject; // -1 for unlimited
    private Long maxStorageMb; // -1 for unlimited

    @Column(length = 1000)
    private String description;

    public SubscriptionPlan() {}

    public SubscriptionPlan(String planName, Double monthlyPrice, Integer maxProjects, Integer maxMembersPerProject, Long maxStorageMb, String description) {
        this.planName = planName != null ? planName.toUpperCase() : "FREE";
        this.monthlyPrice = monthlyPrice;
        this.maxProjects = maxProjects;
        this.maxMembersPerProject = maxMembersPerProject;
        this.maxStorageMb = maxStorageMb;
        this.description = description;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPlanName() { return planName; }
    public void setPlanName(String planName) { this.planName = planName != null ? planName.toUpperCase() : "FREE"; }

    public Double getMonthlyPrice() { return monthlyPrice; }
    public void setMonthlyPrice(Double monthlyPrice) { this.monthlyPrice = monthlyPrice; }

    public Integer getMaxProjects() { return maxProjects; }
    public void setMaxProjects(Integer maxProjects) { this.maxProjects = maxProjects; }

    public Integer getMaxMembersPerProject() { return maxMembersPerProject; }
    public void setMaxMembersPerProject(Integer maxMembersPerProject) { this.maxMembersPerProject = maxMembersPerProject; }

    public Long getMaxStorageMb() { return maxStorageMb; }
    public void setMaxStorageMb(Long maxStorageMb) { this.maxStorageMb = maxStorageMb; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
