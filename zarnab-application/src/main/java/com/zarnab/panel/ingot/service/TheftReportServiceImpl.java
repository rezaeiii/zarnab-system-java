package com.zarnab.panel.ingot.service;

import com.zarnab.panel.auth.model.Role;
import com.zarnab.panel.auth.model.User;
import com.zarnab.panel.common.exception.ZarnabException;
import com.zarnab.panel.core.exception.ExceptionType;
import com.zarnab.panel.ingot.dto.TheftReportDtos;
import com.zarnab.panel.ingot.model.Ingot;
import com.zarnab.panel.ingot.model.TheftReport;
import com.zarnab.panel.ingot.model.TheftReportStatus;
import com.zarnab.panel.ingot.repository.IngotRepository;
import com.zarnab.panel.ingot.repository.TheftReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TheftReportServiceImpl implements TheftReportService {

    private final TheftReportRepository theftReportRepository;
    private final IngotRepository ingotRepository;

    @Override
    public TheftReportDtos.TheftReportResponse createTheftReport(TheftReportDtos.TheftReportRequest request, User user) {
        Ingot ingot = ingotRepository.findById(request.ingotId())
                .orElseThrow(() -> new ZarnabException(ExceptionType.INGOT_NOT_FOUND));

        if (!ingot.getOwner().equals(user)) {
            throw new ZarnabException(ExceptionType.INGOT_OWNERSHIP_ERROR);
        }

        if (theftReportRepository.existsByIngotAndStatusIn(ingot, List.of(TheftReportStatus.PENDING, TheftReportStatus.APPROVED))) {
            throw new ZarnabException(ExceptionType.DUPLICATE_THEFT_REPORT);
        }

        TheftReport theftReport = TheftReport.builder()
                .ingot(ingot)
                .reporter(user)
                .type(request.type())
                .description(request.description())
                .status(TheftReportStatus.PENDING)
                .build();

        return TheftReportDtos.TheftReportResponse.from(theftReportRepository.save(theftReport));
    }

    @Override
    public List<TheftReportDtos.TheftReportResponse> getTheftReports(User user) {
        List<TheftReport> reports;
        if (user.getRoles().stream().anyMatch(role -> role.equals(Role.ADMIN))) {
            reports = theftReportRepository.findAll();
        } else {
            reports = theftReportRepository.findAll().stream()
                    .filter(report -> report.getReporter().equals(user))
                    .collect(Collectors.toList());
        }
        return reports.stream()
                .map(TheftReportDtos.TheftReportResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public TheftReportDtos.TheftReportResponse updateTheftReportStatus(Long reportId, TheftReportDtos.UpdateTheftReportStatusRequest request) {
        TheftReport theftReport = theftReportRepository.findById(reportId)
                .orElseThrow(() -> new ZarnabException(ExceptionType.THEFT_REPORT_NOT_FOUND));

        theftReport.setStatus(request.status());
        return TheftReportDtos.TheftReportResponse.from(theftReportRepository.save(theftReport));
    }
}
