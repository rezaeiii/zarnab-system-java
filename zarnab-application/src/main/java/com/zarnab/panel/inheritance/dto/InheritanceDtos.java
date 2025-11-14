package com.zarnab.panel.inheritance.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.zarnab.panel.clients.dto.FlatPersonInquiryResponse;
import com.zarnab.panel.common.annotation.friendlyDate.FriendlyDate;
import com.zarnab.panel.common.file.annotation.MinioUrl;
import com.zarnab.panel.common.file.annotation.MinioUrlMode;
import com.zarnab.panel.inheritance.model.Heir;
import com.zarnab.panel.inheritance.model.HeirDocumentStatus;
import com.zarnab.panel.inheritance.model.InheritanceCase;
import com.zarnab.panel.inheritance.model.InheritanceStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class InheritanceDtos {

    // == PUBLIC FLOW DTOs == //

    public record InitiateRequest(
            @NotBlank String deceasedIdentifier, // Can be mobile number or national ID
            @NotBlank String initiatorName,
            @NotBlank String initiatorMobile,
            @NotBlank String initiatorBirthDate,
            @NotBlank String initiatorNationalId
            ) {
    }

    public record InitiateResponse(
            String trackingCode,
            String message
    ) {
    }

    public record HeirVerificationRequest(
            @NotBlank String trackingCode,
            @NotBlank String nationalId
    ) {
    }

    public record DeathVerificationRequest(
            @NotBlank String deceasedIdentifier,  // Can be mobile number or national ID
            @NotBlank String requesterNationalId,
            @NotBlank String requesterMobileNumber,
            @NotBlank String requesterBirthDate
    ) {
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record HeirStatusDto(
            String fullName,
            String nationalId,
            HeirDocumentStatus documentStatus,
            Boolean isCurrentUser,
            Boolean isRequester
    ) {
        public static HeirStatusDto fromEntity(Heir heir, String inquirerNationalId) {
            Boolean isCurrent = Objects.equals(heir.getNationalId(), inquirerNationalId) ? true : null;
            return new HeirStatusDto(heir.getFullName(), heir.getNationalId(), heir.getDocumentStatus(), isCurrent, heir.getRequester());
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record DeathVerificationDetailResponse(
            boolean isDeceased,
            String deathStatus, // e.g., DECEASED_CASE_IN_PROGRESS, DECEASED_NO_CASE, NOT_DECEASED
            String trackingCode,
            String deceasedName,
            InheritanceStatus caseStatus,
            List<HeirStatusDto> heirs,
            @FriendlyDate LocalDateTime requestedDate,
//            String initiatorName,
            FlatPersonInquiryResponse requesterInfo // Added requesterInfo
    ) {
    }

    public record CheckStatusRequest(
            @NotBlank String mobileNumber,
            @NotBlank String nationalId,
            @NotBlank String otpCode
    ) {
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record CheckStatusResponse(
            Status status,
            CaseDataDto caseData
    ) {
        public enum Status {
            EXISTING_CASE,
            NEW_USER
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record CaseDataDto(
            String trackingCode,
            String deceasedName,
            InheritanceStatus caseStatus,
            List<HeirStatusDto> heirs
    ) {
    }


    // == ADMIN FLOW DTOs == //

    public record HeirDto(
            Long id,
            String fullName,
            String nationalId,
            @MinioUrl(mode = MinioUrlMode.BOTH)
            String nationalIdCardUrl,
            HeirDocumentStatus documentStatus,
            String adminDocumentComment
    ) {
        public static HeirDto fromEntity(Heir heir) {
            return new HeirDto(
                    heir.getId(),
                    heir.getFullName(),
                    heir.getNationalId(),
                    heir.getNationalIdCardUrl(),
                    heir.getDocumentStatus(),
                    heir.getAdminDocumentComment()
            );
        }
    }

    public record CaseResponse(
            Long id,
            String deceasedUserMobile,
            String deceasedUserFullName,
            String trackingCode,
            InheritanceStatus status,
            String initiatorName,
            String initiatorMobile,
            @MinioUrl(mode = MinioUrlMode.BOTH)
            String deathCertificateUrl,
            @MinioUrl(mode = MinioUrlMode.BOTH)
            String inheritanceCertificateUrl,
            @MinioUrl(mode = MinioUrlMode.BOTH)
            String initiatorNationalIdUrl,
            List<HeirDto> heirs,
            String adminNotes,
            LocalDateTime createdAt
    ) {
        public static CaseResponse fromEntity(InheritanceCase entity) {
            Heir requesterHeir = entity.getRequesterHeir();
            return new CaseResponse(
                    entity.getId(),
                    entity.getDeceasedUser().getMobileNumber(),
                    entity.getDeceasedUser().getNaturalPersonProfile().getFirstName() + " " + entity.getDeceasedUser().getNaturalPersonProfile().getLastName(),
                    entity.getTrackingCode(),
                    entity.getStatus(),
                    requesterHeir.getFullName(),
                    requesterHeir.getMobileNumber(),
                    entity.getDeathCertificateUrl(),
                    entity.getInheritanceCertificateUrl(),
                    requesterHeir.getNationalIdCardUrl(),
                    entity.getHeirs().stream().map(HeirDto::fromEntity).collect(Collectors.toList()),
                    entity.getAdminNotes(),
                    entity.getCreatedAt()
            );
        }
    }

    public record AddHeirsRequest(
            @Size(min = 1) List<HeirDto> heirs
    ) {
    }

    public record UpdateCaseStatusRequest(
            InheritanceStatus status,
            String adminNotes
    ) {
    }

    public record UpdateHeirDocumentStatusRequest(
            HeirDocumentStatus documentStatus,
            String adminDocumentComment
    ) {
    }

    public record InitiateInheritanceOtpRequest(
            @NotBlank String mobileNumber,
            @NotBlank String nationalId
    ) {
    }
}
