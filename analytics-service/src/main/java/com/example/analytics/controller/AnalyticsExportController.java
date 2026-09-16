package com.example.analytics.controller;

import com.example.common.dto.ApiResponse;
import com.example.analytics.dto.AnalyticsDTO;
import com.example.analytics.service.AnalyticsService;
import com.example.analytics.service.ExportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.data.domain.Page;
import java.util.List;

import java.io.IOException;

@RestController
@RequestMapping("/api")
public class AnalyticsExportController {

    private final AnalyticsService analyticsService;
    private final ExportService exportService;

    @Autowired
    public AnalyticsExportController(AnalyticsService analyticsService, ExportService exportService) {
        this.analyticsService = analyticsService;
        this.exportService = exportService;
    }

    @GetMapping("/analytics/history")
    public ApiResponse<List<AnalyticsDTO>> getHistory(@RequestHeader("X-Workspace-Id") Long workspaceId,
            @RequestHeader("X-User") String username, @RequestParam(required=false) Long projectId,
            @RequestParam(defaultValue="0") int page, @RequestParam(defaultValue="20") int size) {
        Page<AnalyticsDTO> history=analyticsService.getHistory(workspaceId,projectId,page,size);
        return ApiResponse.okPage("Analytics history retrieved",history.getContent(),history.getNumber(),history.getSize(),history.getTotalElements(),history.getTotalPages());
    }

    @GetMapping("/analytics")
    public ApiResponse<AnalyticsDTO> getAnalytics(
            @RequestHeader("X-Workspace-Id") Long workspaceId,
            @RequestHeader("X-User") String username,
            @RequestParam(required=false) Long projectId) {
        AnalyticsDTO analytics = analyticsService.getAnalytics(workspaceId, projectId);
        return ApiResponse.ok("Analytics data retrieved successfully", analytics);
    }

    @GetMapping("/export/excel")
    public ResponseEntity<byte[]> exportExcel(@RequestHeader("X-Workspace-Id") Long workspaceId,
            @RequestHeader("X-User") String username,@RequestParam(required=false) Long projectId) throws IOException {
        byte[] data = exportService.exportAnalyticsToExcel(analyticsService.getAnalytics(workspaceId,projectId));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=TaskCraft_Report.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(data);
    }

    @GetMapping("/export/csv")
    public ResponseEntity<byte[]> exportCsv(@RequestHeader("X-Workspace-Id") Long workspaceId,
            @RequestHeader("X-User") String username,@RequestParam(required=false) Long projectId) {
        byte[] data = exportService.exportAnalyticsToCsv(analyticsService.getAnalytics(workspaceId,projectId));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=TaskCraft_Report.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(data);
    }
}
