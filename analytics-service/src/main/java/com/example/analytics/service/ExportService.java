package com.example.analytics.service;

import com.example.analytics.dto.AnalyticsDTO;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
public class ExportService {

    public byte[] exportAnalyticsToExcel(AnalyticsDTO analytics) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Analytics");

            // Header Row
            Row headerRow = sheet.createRow(0);
            String[] columns = {"Metric", "Value"};

            CellStyle headerCellStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerCellStyle.setFont(headerFont);

            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerCellStyle);
            }

            int row = 1;
            row = metric(sheet, row, "Workspace ID", analytics.getWorkspaceId());
            row = metric(sheet, row, "Project ID", analytics.getProjectId() == null ? "ALL" : analytics.getProjectId());
            row = metric(sheet, row, "Total tasks", analytics.getTotalTasks());
            row = metric(sheet, row, "Completed tasks", analytics.getCompletedTasks());
            row = metric(sheet, row, "Overdue tasks", analytics.getOverdueTasks());
            row = metric(sheet, row, "Completion rate (%)", analytics.getCompletionRate());
            row = metric(sheet, row, "Average cycle time (hours)", analytics.getAverageCycleTimeHours());
            row = metric(sheet, row, "Average lead time (hours)", analytics.getAverageLeadTimeHours());
            for (Map.Entry<String,Long> entry : analytics.getStatusDistribution().entrySet()) row = metric(sheet,row,"Status: "+entry.getKey(),entry.getValue());
            for (Map.Entry<String,Long> entry : analytics.getWorkloadByAssignee().entrySet()) row = metric(sheet,row,"Workload: "+entry.getKey(),entry.getValue());

            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        }
    }

    public byte[] exportAnalyticsToCsv(AnalyticsDTO analytics) {
        StringBuilder csv = new StringBuilder();
        csv.append("Metric,Value\n");
        csv(csv,"Workspace ID",analytics.getWorkspaceId()); csv(csv,"Project ID",analytics.getProjectId()==null?"ALL":analytics.getProjectId());
        csv(csv,"Total tasks",analytics.getTotalTasks()); csv(csv,"Completed tasks",analytics.getCompletedTasks()); csv(csv,"Overdue tasks",analytics.getOverdueTasks());
        csv(csv,"Completion rate (%)",analytics.getCompletionRate()); csv(csv,"Average cycle time (hours)",analytics.getAverageCycleTimeHours()); csv(csv,"Average lead time (hours)",analytics.getAverageLeadTimeHours());
        analytics.getStatusDistribution().forEach((key,value)->csv(csv,"Status: "+key,value));
        analytics.getWorkloadByAssignee().forEach((key,value)->csv(csv,"Workload: "+key,value));
        return csv.toString().getBytes(StandardCharsets.UTF_8);
    }

    private int metric(Sheet sheet,int index,String name,Object value){Row row=sheet.createRow(index);row.createCell(0).setCellValue(name);row.createCell(1).setCellValue(String.valueOf(value));return index+1;}
    private void csv(StringBuilder target,String name,Object value){target.append('"').append(name.replace("\"","\"\"")).append("\",").append(value).append('\n');}
}
