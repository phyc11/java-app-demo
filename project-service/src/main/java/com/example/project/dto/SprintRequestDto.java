package com.example.project.dto;

import java.time.LocalDate;

public class SprintRequestDto {
    private String name;
    private String goal;
    private LocalDate startDate;
    private LocalDate endDate;

    public SprintRequestDto() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getGoal() { return goal; }
    public void setGoal(String goal) { this.goal = goal; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
}
