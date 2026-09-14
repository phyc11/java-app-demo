package com.example.project.dto;

import com.example.project.model.*;

import java.util.List;

public class ProjectDetailDto {
    private Project project;
    private List<ProjectMember> members;
    private List<Sprint> sprints;
    private List<Milestone> milestones;
    private List<ProjectTag> tags;

    public ProjectDetailDto() {}

    public ProjectDetailDto(Project project, List<ProjectMember> members, List<Sprint> sprints, List<Milestone> milestones, List<ProjectTag> tags) {
        this.project = project;
        this.members = members;
        this.sprints = sprints;
        this.milestones = milestones;
        this.tags = tags;
    }

    public Project getProject() { return project; }
    public void setProject(Project project) { this.project = project; }

    public List<ProjectMember> getMembers() { return members; }
    public void setMembers(List<ProjectMember> members) { this.members = members; }

    public List<Sprint> getSprints() { return sprints; }
    public void setSprints(List<Sprint> sprints) { this.sprints = sprints; }

    public List<Milestone> getMilestones() { return milestones; }
    public void setMilestones(List<Milestone> milestones) { this.milestones = milestones; }

    public List<ProjectTag> getTags() { return tags; }
    public void setTags(List<ProjectTag> tags) { this.tags = tags; }
}
