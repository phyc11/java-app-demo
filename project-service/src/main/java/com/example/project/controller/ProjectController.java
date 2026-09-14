package com.example.project.controller;

import com.example.common.dto.ApiResponse;
import com.example.project.dto.*;
import com.example.project.model.*;
import com.example.project.service.ProjectService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Project>> createProject(@RequestBody ProjectRequestDto request) {
        Project project = projectService.createProject(request);
        return ResponseEntity.ok(ApiResponse.ok("Project created successfully", project));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Project>>> getAllProjects() {
        List<Project> projects = projectService.getAllProjects();
        return ResponseEntity.ok(ApiResponse.ok("Retrieved " + projects.size() + " projects", projects));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProjectDetailDto>> getProjectDetail(@PathVariable Long id) {
        ProjectDetailDto detail = projectService.getProjectDetail(id);
        return ResponseEntity.ok(ApiResponse.ok("Project details retrieved", detail));
    }

    @PostMapping("/{id}/members")
    public ResponseEntity<ApiResponse<ProjectMember>> addOrUpdateMember(
            @PathVariable Long id,
            @RequestBody ProjectMemberRequestDto request) {

        ProjectMember member = projectService.addOrUpdateMember(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Project member role updated successfully", member));
    }

    @DeleteMapping("/{id}/members/{username}")
    public ResponseEntity<ApiResponse<Void>> removeMember(
            @PathVariable Long id,
            @PathVariable String username) {

        projectService.removeMember(id, username);
        return ResponseEntity.ok(ApiResponse.ok("Member removed from project", null));
    }

    @PostMapping("/{id}/sprints")
    public ResponseEntity<ApiResponse<Sprint>> createSprint(
            @PathVariable Long id,
            @RequestBody SprintRequestDto request) {

        Sprint sprint = projectService.createSprint(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Sprint created successfully", sprint));
    }

    @PutMapping("/sprints/{sprintId}/status")
    public ResponseEntity<ApiResponse<Sprint>> updateSprintStatus(
            @PathVariable Long sprintId,
            @RequestParam String status) {

        Sprint sprint = projectService.updateSprintStatus(sprintId, status);
        return ResponseEntity.ok(ApiResponse.ok("Sprint status updated to " + status, sprint));
    }

    @PostMapping("/{id}/milestones")
    public ResponseEntity<ApiResponse<Milestone>> createMilestone(
            @PathVariable Long id,
            @RequestBody MilestoneRequestDto request) {

        Milestone milestone = projectService.createMilestone(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Milestone created successfully", milestone));
    }

    @PutMapping("/milestones/{milestoneId}/status")
    public ResponseEntity<ApiResponse<Milestone>> updateMilestoneStatus(
            @PathVariable Long milestoneId,
            @RequestParam String status) {

        Milestone milestone = projectService.updateMilestoneStatus(milestoneId, status);
        return ResponseEntity.ok(ApiResponse.ok("Milestone status updated to " + status, milestone));
    }

    @PostMapping("/{id}/tags")
    public ResponseEntity<ApiResponse<ProjectTag>> createTag(
            @PathVariable Long id,
            @RequestBody ProjectTagRequestDto request) {

        ProjectTag tag = projectService.createTag(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Project tag created successfully", tag));
    }

    @DeleteMapping("/tags/{tagId}")
    public ResponseEntity<ApiResponse<Void>> deleteTag(@PathVariable Long tagId) {
        projectService.deleteTag(tagId);
        return ResponseEntity.ok(ApiResponse.ok("Project tag deleted", null));
    }
}
