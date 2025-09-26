package com.zarnab.panel.ingot.service;

import com.zarnab.panel.auth.model.User;
import com.zarnab.panel.ingot.dto.TheftReportDtos;

import java.util.List;

public interface TheftReportService {
    TheftReportDtos.TheftReportResponse createTheftReport(TheftReportDtos.TheftReportRequest request, User user);

    List<TheftReportDtos.TheftReportResponse> getTheftReports(User user);

    TheftReportDtos.TheftReportResponse updateTheftReportStatus(Long reportId, TheftReportDtos.UpdateTheftReportStatusRequest request);
}
