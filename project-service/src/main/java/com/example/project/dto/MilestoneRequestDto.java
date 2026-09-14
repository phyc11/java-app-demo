package com.example.project.dto;

import java.time.LocalDate;

public class MilestoneRequestDto {
    private String title;
    private String description;
    private LocalDate dueDate;

    public MilestoneRequestDto() {}

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
}
