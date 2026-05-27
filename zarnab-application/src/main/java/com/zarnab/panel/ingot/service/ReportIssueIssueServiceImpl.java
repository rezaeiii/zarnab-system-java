package com.zarnab.panel.ingot.service;

import com.zarnab.panel.auth.model.Role;
import com.zarnab.panel.auth.model.User;
import com.zarnab.panel.common.exception.ZarnabException;
import com.zarnab.panel.common.search.SpecificationBuilder;
import com.zarnab.panel.core.dto.req.PageableRequest;
import com.zarnab.panel.core.dto.res.PageableResponse;
import com.zarnab.panel.core.exception.ExceptionType;
import com.zarnab.panel.core.util.RoleUtil;
import com.zarnab.panel.ingot.dto.ReportIssueDtos;
import com.zarnab.panel.ingot.model.Ingot;
import com.zarnab.panel.ingot.model.ReportIssue;
import com.zarnab.panel.ingot.model.ReportIssueStatus;
import com.zarnab.panel.ingot.repository.IngotRepository;
import com.zarnab.panel.ingot.repository.ReportIssueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportIssueIssueServiceImpl implements ReportIssueService {

    private final ReportIssueRepository reportIssueRepository;
    private final IngotRepository ingotRepository;

    @Override
    public ReportIssueDtos.TheftReportResponse createTheftReport(ReportIssueDtos.TheftReportRequest request, User user) {
        Ingot ingot = ingotRepository.findById(request.ingotId())
                .orElseThrow(() -> new ZarnabException(ExceptionType.INGOT_NOT_FOUND));

        if (ingot.getOwner() == null) {
            throw new ZarnabException(ExceptionType.INVALID_THEFT_REPORT);
        }

        if (!ingot.getOwner().getId().equals(user.getId())) {
            throw new ZarnabException(ExceptionType.INGOT_OWNERSHIP_ERROR);
        }

        if (reportIssueRepository.existsByIngotAndStatusIn(ingot, List.of(ReportIssueStatus.PENDING, ReportIssueStatus.APPROVED))) {
            throw new ZarnabException(ExceptionType.DUPLICATE_THEFT_REPORT);
        }

        ReportIssue reportIssue = ReportIssue.builder()
                .ingot(ingot)
                .reporter(user)
                .type(request.type())
                .description(request.description())
                .status(ReportIssueStatus.PENDING)
                .build();

        return ReportIssueDtos.TheftReportResponse.from(reportIssueRepository.save(reportIssue));
    }

    @Override
    public PageableResponse<ReportIssueDtos.TheftReportResponse> getReportIssues(User user, PageableRequest pageableRequest) {
        pageableRequest.addToAliases("serial", "ingot.serial");
        pageableRequest.addToAliases("reporter", "reporter.naturalPersonProfile.firstName");
        pageableRequest.addToAliases("status", "status");

        Specification<ReportIssue> spec = SpecificationBuilder.buildSpecification(pageableRequest);

        boolean isAdmin = RoleUtil.hasActiveRole(user, Role.ADMIN);
        if (!isAdmin) {
            Specification<ReportIssue> userSecuritySpec = (root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("reporter"), user);
            spec = (spec == null) ? userSecuritySpec : spec.and(userSecuritySpec);
        }

        Pageable pageable = PageRequest.of(pageableRequest.getPage(), pageableRequest.getSize(), pageableRequest.getSort());

        Page<ReportIssue> reportIssuePage = reportIssueRepository.findAll(spec, pageable);

        List<ReportIssueDtos.TheftReportResponse> reportIssueDtos = reportIssuePage.getContent().stream()
                .map(ReportIssueDtos.TheftReportResponse::from)
                .collect(Collectors.toList());

        return new PageableResponse<>(
                reportIssueDtos,
                reportIssuePage.getTotalElements(),
                reportIssuePage.getNumber(),
                reportIssuePage.getSize()
        );
    }

    @Override
//    @PreAuthorize("hasRole('ADMIN')")
    public ReportIssueDtos.TheftReportResponse updateTheftReportStatus(Long reportId, ReportIssueStatus status) {
        ReportIssue reportIssue = reportIssueRepository.findById(reportId)
                .orElseThrow(() -> new ZarnabException(ExceptionType.THEFT_REPORT_NOT_FOUND));

        reportIssue.setStatus(status);
        return ReportIssueDtos.TheftReportResponse.from(reportIssueRepository.save(reportIssue));
    }
}
