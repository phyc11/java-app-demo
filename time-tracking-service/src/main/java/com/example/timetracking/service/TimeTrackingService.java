package com.example.timetracking.service;

import com.example.timetracking.dto.*;
import com.example.timetracking.model.TaskEstimate;
import com.example.timetracking.model.Worklog;
import com.example.timetracking.repository.TaskEstimateRepository;
import com.example.timetracking.repository.WorklogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class TimeTrackingService {

    private final WorklogRepository worklogRepository;
    private final TaskEstimateRepository taskEstimateRepository;

    public TimeTrackingService(WorklogRepository worklogRepository, TaskEstimateRepository taskEstimateRepository) {
        this.worklogRepository = worklogRepository;
        this.taskEstimateRepository = taskEstimateRepository;
    }

    @Transactional
    public Worklog logWork(WorklogRequestDto request) {
        if (request.getTaskId() == null) {
            throw new IllegalArgumentException("Task ID is required for worklog entry!");
        }
        if (request.getTimeSpentHours() == null || request.getTimeSpentHours() <= 0) {
            throw new IllegalArgumentException("Time spent must be greater than 0 hours!");
        }
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("Username is required!");
        }

        LocalDate date = request.getLogDate() != null ? request.getLogDate() : LocalDate.now();
        Worklog worklog = new Worklog(
                request.getTaskId(),
                request.getProjectId(),
                request.getUsername(),
                request.getTimeSpentHours(),
                request.getDescription(),
                date
        );
        return worklogRepository.save(worklog);
    }

    @Transactional
    public TaskEstimate setTaskEstimate(Long taskId, Long projectId, Double estimatedHours) {
        if (taskId == null) {
            throw new IllegalArgumentException("Task ID is required!");
        }
        if (estimatedHours == null || estimatedHours < 0) {
            throw new IllegalArgumentException("Estimated hours cannot be negative!");
        }

        TaskEstimate estimate = taskEstimateRepository.findById(taskId)
                .orElse(new TaskEstimate(taskId, projectId, estimatedHours));

        estimate.setProjectId(projectId);
        estimate.setEstimatedHours(estimatedHours);
        return taskEstimateRepository.save(estimate);
    }

    public List<Worklog> getWorklogsByTask(Long taskId) {
        return worklogRepository.findByTaskId(taskId);
    }

    public TaskTimeSummaryDto getTaskSummary(Long taskId) {
        Double logged = worklogRepository.sumTimeSpentByTaskId(taskId);
        TaskEstimate estimate = taskEstimateRepository.findById(taskId).orElse(null);
        Double estimated = estimate != null ? estimate.getEstimatedHours() : 0.0;

        return new TaskTimeSummaryDto(taskId, estimated, logged);
    }

    public UserProductivityReportDto getUserProductivityReport(String username) {
        Double totalHours = worklogRepository.sumTimeSpentByUsername(username);
        Long taskCount = worklogRepository.countDistinctTasksByUsername(username);
        Long logCount = worklogRepository.countLogsByUsername(username);

        return new UserProductivityReportDto(username, totalHours, taskCount, logCount);
    }

    public ProjectTimeReportDto getProjectTimeReport(Long projectId) {
        Double totalEstimated = taskEstimateRepository.sumEstimatedHoursByProjectId(projectId);
        Double totalLogged = worklogRepository.sumTimeSpentByProjectId(projectId);
        Long taskCount = worklogRepository.countDistinctTasksByProjectId(projectId);

        return new ProjectTimeReportDto(projectId, totalEstimated, totalLogged, taskCount);
    }

    @Transactional
    public void deleteWorklog(Long worklogId) {
        worklogRepository.deleteById(worklogId);
    }
}
