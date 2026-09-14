package com.example.analytics.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
public class ExportService {

    public byte[] exportTasksToExcel() throws IOException {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Task Report");

            // Header Row
            Row headerRow = sheet.createRow(0);
            String[] columns = {"ID", "Title", "Status", "Priority", "Category", "Created By"};

            CellStyle headerCellStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerCellStyle.setFont(headerFont);

            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerCellStyle);
            }

            // Demo Rows
            Row row1 = sheet.createRow(1);
            row1.createCell(0).setCellValue(1);
            row1.createCell(1).setCellValue("Setup Microservices Architecture");
            row1.createCell(2).setCellValue("IN_PROGRESS");
            row1.createCell(3).setCellValue("HIGH");
            row1.createCell(4).setCellValue("Backend Development");
            row1.createCell(5).setCellValue("Admin");

            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        }
    }

    public byte[] exportTasksToCsv() {
        StringBuilder csv = new StringBuilder();
        csv.append("ID,Title,Status,Priority,Category,Created By\n");
        csv.append("1,\"Setup Microservices Architecture\",IN_PROGRESS,HIGH,\"Backend Development\",Admin\n");
        return csv.toString().getBytes(StandardCharsets.UTF_8);
    }
}
