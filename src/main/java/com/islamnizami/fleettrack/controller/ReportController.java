package com.islamnizami.fleettrack.controller;

import com.islamnizami.fleettrack.service.PdfReportService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final PdfReportService reportService;

    @Operation(summary = "Download the fleet report (PDF)",
            description = "Fleet status, vehicles, maintenance log and driver activity.")
    @GetMapping(value = "/fleet", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> fleetReport() {
        byte[] pdf = reportService.generateFleetReport();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename("fleet-report.pdf").build().toString())
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
