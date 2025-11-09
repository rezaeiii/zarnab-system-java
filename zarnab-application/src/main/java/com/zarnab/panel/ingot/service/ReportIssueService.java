package com.zarnab.panel.ingot.service;

import com.zarnab.panel.auth.model.User;
import com.zarnab.panel.core.dto.req.PageableRequest;
import com.zarnab.panel.core.dto.res.PageableResponse;
import com.zarnab.panel.ingot.dto.ReportIssueDtos;
import com.zarnab.panel.ingot.model.ReportIssueStatus;

public interface ReportIssueService {
    ReportIssueDtos.TheftReportResponse createTheftReport(ReportIssueDtos.TheftReportRequest request, User user);

    PageableResponse<ReportIssueDtos.TheftReportResponse> getReportIssues(User user, PageableRequest pageableRequest);

    ReportIssueDtos.TheftReportResponse updateTheftReportStatus(Long reportId, ReportIssueStatus status);
}
