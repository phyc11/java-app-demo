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

    @GetMapping("/analytics")
    public ApiResponse<AnalyticsDTO> getAnalytics() {
        AnalyticsDTO analytics = analyticsService.getAnalytics();
        return ApiResponse.ok("Analytics data retrieved successfully", analytics);
    }

    @GetMapping("/export/excel")
    public ResponseEntity<byte[]> exportExcel() throws IOException {
        byte[] data = exportService.exportTasksToExcel();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=TaskCraft_Report.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(data);
    }

    @GetMapping("/export/csv")
    public ResponseEntity<byte[]> exportCsv() {
        byte[] data = exportService.exportTasksToCsv();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=TaskCraft_Report.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(data);
    }
}
