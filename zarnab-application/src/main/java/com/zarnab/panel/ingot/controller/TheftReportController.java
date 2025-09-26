package com.zarnab.panel.ingot.controller;

import com.zarnab.panel.auth.model.User;
import com.zarnab.panel.ingot.dto.TheftReportDtos;
import com.zarnab.panel.ingot.service.TheftReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/theft-reports")
@RequiredArgsConstructor
public class TheftReportController {

    private final TheftReportService theftReportService;

    @PostMapping
    public ResponseEntity<TheftReportDtos.TheftReportResponse> createTheftReport(@RequestBody TheftReportDtos.TheftReportRequest request, @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(theftReportService.createTheftReport(request, user));
    }

    @GetMapping
    public ResponseEntity<List<TheftReportDtos.TheftReportResponse>> getTheftReports(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(theftReportService.getTheftReports(user));
    }

    @PutMapping("/{reportId}/status")
    public ResponseEntity<TheftReportDtos.TheftReportResponse> updateTheftReportStatus(@PathVariable Long reportId, @RequestBody TheftReportDtos.UpdateTheftReportStatusRequest request) {
        return ResponseEntity.ok(theftReportService.updateTheftReportStatus(reportId, request));
    }
}
