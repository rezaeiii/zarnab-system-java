package com.zarnab.panel.ingot.controller;

import com.zarnab.panel.auth.model.User;
import com.zarnab.panel.core.annotations.PageableParam;
import com.zarnab.panel.core.dto.req.PageableRequest;
import com.zarnab.panel.core.dto.res.PageableResponse;
import com.zarnab.panel.ingot.dto.ReportIssueDtos;
import com.zarnab.panel.ingot.model.ReportIssueStatus;
import com.zarnab.panel.ingot.service.ReportIssueService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/api/report-issue")
@RequiredArgsConstructor
public class ReportIssueController {

    private final ReportIssueService reportIssueService;

    @PostMapping
    public ResponseEntity<ReportIssueDtos.TheftReportResponse> createTheftReport(@RequestBody ReportIssueDtos.TheftReportRequest request, @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(reportIssueService.createTheftReport(request, user));
    }

    @Operation(summary = "List report issues with pagination, filtering, and sorting")
    @GetMapping
    public ResponseEntity<PageableResponse<ReportIssueDtos.TheftReportResponse>> getReportIssues(
            @AuthenticationPrincipal User user,
            @PageableParam @ParameterObject PageableRequest pageableRequest) {
        return ResponseEntity.ok(reportIssueService.getReportIssues(user, pageableRequest));
    }

    @PutMapping("/{reportId}/status/{status}")
    public ResponseEntity<ReportIssueDtos.TheftReportResponse> updateTheftReportStatus(@PathVariable Long reportId,
                                                                                       @PathVariable ReportIssueStatus status) {
        return ResponseEntity.ok(reportIssueService.updateTheftReportStatus(reportId, status));
    }
}
