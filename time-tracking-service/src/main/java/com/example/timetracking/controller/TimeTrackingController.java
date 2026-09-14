package com.example.timetracking.controller;

import com.example.common.dto.ApiResponse;
import com.example.timetracking.dto.*;
import com.example.timetracking.model.TaskEstimate;
import com.example.timetracking.model.Worklog;
import com.example.timetracking.service.TimeTrackingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/time-tracking")
public class TimeTrackingController {

    private final TimeTrackingService timeTrackingService;

    public TimeTrackingController(TimeTrackingService timeTrackingService) {
        this.timeTrackingService = timeTrackingService;
    }

    @PostMapping("/worklogs")
    public ResponseEntity<ApiResponse<Worklog>> logWork(@RequestBody WorklogRequestDto request) {
        Worklog logged = timeTrackingService.logWork(request);
        return ResponseEntity.ok(ApiResponse.ok("Worklog recorded successfully", logged));
    }

    @GetMapping("/worklogs/task/{taskId}")
    public ResponseEntity<ApiResponse<List<Worklog>>> getWorklogsByTask(@PathVariable Long taskId) {
        List<Worklog> worklogs = timeTrackingService.getWorklogsByTask(taskId);
        return ResponseEntity.ok(ApiResponse.ok("Worklogs retrieved for task #" + taskId, worklogs));
    }

    @PostMapping("/estimates")
    public ResponseEntity<ApiResponse<TaskEstimate>> setEstimate(
            @RequestParam Long taskId,
            @RequestParam(required = false) Long projectId,
            @RequestParam Double estimatedHours) {

        TaskEstimate estimate = timeTrackingService.setTaskEstimate(taskId, projectId, estimatedHours);
        return ResponseEntity.ok(ApiResponse.ok("Task estimate updated successfully", estimate));
    }

    @GetMapping("/summary/task/{taskId}")
    public ResponseEntity<ApiResponse<TaskTimeSummaryDto>> getTaskSummary(@PathVariable Long taskId) {
        TaskTimeSummaryDto summary = timeTrackingService.getTaskSummary(taskId);
        return ResponseEntity.ok(ApiResponse.ok("Task time summary retrieved", summary));
    }

    @GetMapping("/reports/user/{username}")
    public ResponseEntity<ApiResponse<UserProductivityReportDto>> getUserReport(@PathVariable String username) {
        UserProductivityReportDto report = timeTrackingService.getUserProductivityReport(username);
        return ResponseEntity.ok(ApiResponse.ok("User productivity report generated", report));
    }

    @GetMapping("/reports/project/{projectId}")
    public ResponseEntity<ApiResponse<ProjectTimeReportDto>> getProjectReport(@PathVariable Long projectId) {
        ProjectTimeReportDto report = timeTrackingService.getProjectTimeReport(projectId);
        return ResponseEntity.ok(ApiResponse.ok("Project time report generated", report));
    }

    @DeleteMapping("/worklogs/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteWorklog(@PathVariable Long id) {
        timeTrackingService.deleteWorklog(id);
        return ResponseEntity.ok(ApiResponse.ok("Worklog entry deleted successfully", null));
    }
}
