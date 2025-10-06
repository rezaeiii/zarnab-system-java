package com.zarnab.panel.ingot.dto;

import com.zarnab.panel.common.annotation.friendlyDate.FriendlyDate;
import com.zarnab.panel.ingot.model.TheftReport;
import com.zarnab.panel.ingot.model.TheftReportStatus;
import com.zarnab.panel.ingot.model.TheftReportType;

import java.time.LocalDateTime;

public class TheftReportDtos {

    public record TheftReportRequest(
            Long ingotId,
            TheftReportType type,
            String description
    ) {
    }

    public record TheftReportResponse(
            Long id,
            IngotDtos.IngotResponse ingot,
            IngotDtos.UserDto reporter,
            TheftReportType type,
            String description,
            @FriendlyDate
            LocalDateTime reportDate,
            TheftReportStatus status
    ) {
        public static TheftReportResponse from(TheftReport theftReport) {
            return new TheftReportResponse(
                    theftReport.getId(),
                    IngotDtos.IngotResponse.from(theftReport.getIngot()),
                    new IngotDtos.UserDto(
                            theftReport.getReporter().getId(),
                            theftReport.getReporter().getMobileNumber(),
                            theftReport.getReporter().getNaturalPersonProfile() != null ? theftReport.getReporter().getNaturalPersonProfile().getFirstName() : null,
                            theftReport.getReporter().getNaturalPersonProfile() != null ? theftReport.getReporter().getNaturalPersonProfile().getLastName() : null
                    ),
                    theftReport.getType(),
                    theftReport.getDescription(),
                    theftReport.getCreatedAt(),
                    theftReport.getStatus()
            );
        }
    }

    public record UpdateTheftReportStatusRequest(
            TheftReportStatus status
    ) {
    }
}
