package com.zarnab.panel.ingot.dto;

import com.zarnab.panel.common.annotation.friendlyDate.FriendlyDate;
import com.zarnab.panel.ingot.model.ReportIssue;
import com.zarnab.panel.ingot.model.ReportIssueStatus;
import com.zarnab.panel.ingot.model.ReportIssueType;

import java.time.LocalDateTime;

public class ReportIssueDtos {

    public record TheftReportRequest(
            Long ingotId,
            ReportIssueType type,
            String description
    ) {
    }

    public record TheftReportResponse(
            Long id,
            IngotDtos.IngotResponse ingot,
            IngotDtos.UserDto reporter,
            ReportIssueType type,
            String description,
            @FriendlyDate
            LocalDateTime reportDate,
            ReportIssueStatus status
    ) {
        public static TheftReportResponse from(ReportIssue reportIssue) {
            return new TheftReportResponse(
                    reportIssue.getId(),
                    IngotDtos.IngotResponse.from(reportIssue.getIngot()),
                    new IngotDtos.UserDto(
                            reportIssue.getReporter().getId(),
                            reportIssue.getReporter().getMobileNumber(),
                            reportIssue.getReporter().getNaturalPersonProfile() != null ? reportIssue.getReporter().getNaturalPersonProfile().getFirstName() : null,
                            reportIssue.getReporter().getNaturalPersonProfile() != null ? reportIssue.getReporter().getNaturalPersonProfile().getLastName() : null
                    ),
                    reportIssue.getType(),
                    reportIssue.getDescription(),
                    reportIssue.getCreatedAt(),
                    reportIssue.getStatus()
            );
        }
    }

    public record UpdateTheftReportStatusRequest(
            ReportIssueStatus status
    ) {
    }
}
