package com.example.project.model;

import javax.persistence.*;

@Entity
@Table(name = "project_tags", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"projectId", "name"})
})
public class ProjectTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long projectId;

    @Column(nullable = false)
    private String name;

    private String color; // e.g. #FF5733

    public ProjectTag() {}

    public ProjectTag(Long projectId, String name, String color) {
        this.projectId = projectId;
        this.name = name;
        this.color = color != null ? color : "#6C757D";
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
}
