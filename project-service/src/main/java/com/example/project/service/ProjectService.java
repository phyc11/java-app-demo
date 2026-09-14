package com.example.project.service;

import com.example.project.dto.*;
import com.example.project.model.*;
import com.example.project.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final SprintRepository sprintRepository;
    private final MilestoneRepository milestoneRepository;
    private final ProjectTagRepository projectTagRepository;

    public ProjectService(ProjectRepository projectRepository,
                          ProjectMemberRepository projectMemberRepository,
                          SprintRepository sprintRepository,
                          MilestoneRepository milestoneRepository,
                          ProjectTagRepository projectTagRepository) {
        this.projectRepository = projectRepository;
        this.projectMemberRepository = projectMemberRepository;
        this.sprintRepository = sprintRepository;
        this.milestoneRepository = milestoneRepository;
        this.projectTagRepository = projectTagRepository;
    }

    @Transactional
    public Project createProject(ProjectRequestDto request) {
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Project name is required!");
        }
        if (request.getProjectKey() == null || request.getProjectKey().trim().isEmpty()) {
            throw new IllegalArgumentException("Project key is required!");
        }
        if (request.getOwnerUsername() == null || request.getOwnerUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("Owner username is required!");
        }

        String key = request.getProjectKey().trim().toUpperCase();
        if (projectRepository.findByProjectKey(key).isPresent()) {
            throw new IllegalArgumentException("Project key '" + key + "' already exists!");
        }

        Project project = new Project(
                request.getName().trim(),
                key,
                request.getDescription(),
                request.getOwnerUsername().trim()
        );
        project = projectRepository.save(project);

        // Add owner as OWNER member
        ProjectMember ownerMember = new ProjectMember(project.getId(), request.getOwnerUsername().trim(), "OWNER");
        projectMemberRepository.save(ownerMember);

        return project;
    }

    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    public ProjectDetailDto getProjectDetail(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found with ID: " + projectId));

        List<ProjectMember> members = projectMemberRepository.findByProjectId(projectId);
        List<Sprint> sprints = sprintRepository.findByProjectId(projectId);
        List<Milestone> milestones = milestoneRepository.findByProjectId(projectId);
        List<ProjectTag> tags = projectTagRepository.findByProjectId(projectId);

        return new ProjectDetailDto(project, members, sprints, milestones, tags);
    }

    @Transactional
    public ProjectMember addOrUpdateMember(Long projectId, ProjectMemberRequestDto request) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found with ID: " + projectId));

        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("Username is required!");
        }

        String role = request.getRole() != null ? request.getRole().toUpperCase() : "MEMBER";
        List<String> validRoles = List.of("OWNER", "PROJECT_LEAD", "MEMBER", "VIEWER");
        if (!validRoles.contains(role)) {
            throw new IllegalArgumentException("Invalid role: " + role + ". Allowed roles: " + validRoles);
        }

        ProjectMember member = projectMemberRepository.findByProjectIdAndUsername(projectId, request.getUsername().trim())
                .orElse(new ProjectMember(projectId, request.getUsername().trim(), role));

        member.setRole(role);
        return projectMemberRepository.save(member);
    }

    @Transactional
    public void removeMember(Long projectId, String username) {
        projectMemberRepository.deleteByProjectIdAndUsername(projectId, username);
    }

    @Transactional
    public Sprint createSprint(Long projectId, SprintRequestDto request) {
        projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found with ID: " + projectId));

        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Sprint name is required!");
        }

        Sprint sprint = new Sprint(
                projectId,
                request.getName().trim(),
                request.getGoal(),
                request.getStartDate(),
                request.getEndDate(),
                "PLANNED"
        );
        return sprintRepository.save(sprint);
    }

    @Transactional
    public Sprint updateSprintStatus(Long sprintId, String status) {
        Sprint sprint = sprintRepository.findById(sprintId)
                .orElseThrow(() -> new IllegalArgumentException("Sprint not found with ID: " + sprintId));

        sprint.setStatus(status.toUpperCase());
        return sprintRepository.save(sprint);
    }

    @Transactional
    public Milestone createMilestone(Long projectId, MilestoneRequestDto request) {
        projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found with ID: " + projectId));

        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Milestone title is required!");
        }

        Milestone milestone = new Milestone(
                projectId,
                request.getTitle().trim(),
                request.getDescription(),
                request.getDueDate()
        );
        return milestoneRepository.save(milestone);
    }

    @Transactional
    public Milestone updateMilestoneStatus(Long milestoneId, String status) {
        Milestone milestone = milestoneRepository.findById(milestoneId)
                .orElseThrow(() -> new IllegalArgumentException("Milestone not found with ID: " + milestoneId));

        milestone.setStatus(status.toUpperCase());
        return milestoneRepository.save(milestone);
    }

    @Transactional
    public ProjectTag createTag(Long projectId, ProjectTagRequestDto request) {
        projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found with ID: " + projectId));

        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Tag name is required!");
        }

        ProjectTag tag = new ProjectTag(projectId, request.getName().trim(), request.getColor());
        return projectTagRepository.save(tag);
    }

    @Transactional
    public void deleteTag(Long tagId) {
        projectTagRepository.deleteById(tagId);
    }
}
