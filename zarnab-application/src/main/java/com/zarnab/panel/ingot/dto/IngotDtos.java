package com.zarnab.panel.ingot.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zarnab.panel.auth.model.Role;
import com.zarnab.panel.auth.model.User;
import com.zarnab.panel.common.annotation.friendlyDate.FriendlyDate;
import com.zarnab.panel.core.util.RoleUtil;
import com.zarnab.panel.ingot.model.*;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class IngotDtos {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public record IngotCreateRequest(
            @Schema(description = "Unique serial", requiredMode = Schema.RequiredMode.REQUIRED)
            String serial,
            @Schema(description = "Manufacture date (YYYY-MM-DD)")
            LocalDate manufactureDate,
            @Schema(description = "Karat (purity)")
            Integer karat,
            @Schema(description = "Weight in grams")
            Double weightGrams
    ) {
    }

    public record IngotResponse(
            Long id,
            String serial,
            @FriendlyDate(includeTime = false)
            LocalDate manufactureDate,
            Integer karat,
            Double weightGrams,
            UserDto owner,
            boolean isTheft, boolean isMissing, boolean isTampering
    ) {
        public static IngotResponse from(Ingot ingot) {
            User owner = ingot.getOwner();
            String firstName = "", lastName = "", mobileNumber = "";
            Long ownerId = null;
            if (owner != null) {
                ownerId = owner.getId();
                firstName = owner.getNaturalPersonProfile() != null ? owner.getNaturalPersonProfile().getFirstName() : null;
                lastName = owner.getNaturalPersonProfile() != null ? owner.getNaturalPersonProfile().getLastName() : null;
                mobileNumber = owner.getMobileNumber();
            }

            mobileNumber = mobileNumber.length() > 10 ? mobileNumber.substring(0, 4) + "*****" + mobileNumber.substring(9, 11) : null;
            return new IngotResponse(
                    ingot.getId(),
                    ingot.getSerial(),
                    ingot.getManufactureDate(),
                    ingot.getKarat(),
                    ingot.getWeightGrams(),
                    new UserDto(ownerId, mobileNumber, firstName, lastName),
                    ingot.isTheft(),
                    ingot.isMissing(),
                    ingot.isTampering()
            );
        }
    }

    public record IngotBatchResponse(
            Long id,
            String productType,
            @FriendlyDate(includeTime = false)
            LocalDate manufactureDate,
            int ingotCount,
            Set<String> lastFiveSerials
    ) {
        public static IngotBatchResponse from(IngotBatch batch) {
            return new IngotBatchResponse(
                    batch.getId(),
                    batch.getIngots().getFirst().getProductType().getPersianName(),
                    batch.getCreatedAt().toLocalDate(),
                    batch.getIngotCount(),
                    batch.getLastFiveSerials() != null
                            ? Arrays.stream(batch.getLastFiveSerials().split(",")).collect(Collectors.toSet())
                            : new HashSet<>()
            );
        }
    }

    public record UserDto(Long id,
                          String mobile,
                          String firstName,
                          String lastName) {
    }

    public record TransferDto(
            Long id,
            IngotResponse ingot,
            UserDto seller,
            UserDto buyer,
            String buyerMobileNumber,
            TransferStatus status,
            @FriendlyDate
            LocalDateTime transferAt,
            @FriendlyDate
            LocalDateTime lastUpdateAt,
            boolean buyerIsYou,
            boolean sellerIsYou
    ) {
        public static TransferDto from(Transfer transfer, User user) {
            User seller = transfer.getSeller();
            User buyer = transfer.getBuyer();
            boolean admin = RoleUtil.hasActiveRole(user, Role.ADMIN);
            var buyerIsYou = user != null && ((transfer.getBuyerMobileNumber() != null && transfer.getBuyerMobileNumber().equals(user.getMobileNumber()))
                                              || (admin && buyer == null));
            var sellerIsYou = user != null && ((admin && seller == null) || (seller != null && seller.getId().equals(user.getId())));
            return new TransferDto(
                    transfer.getId(),
                    IngotResponse.from(transfer.getIngot()),
                    seller != null ? new UserDto(seller.getId(), seller.getMobileNumber(), seller.getNaturalPersonProfile() != null
                            ? seller.getNaturalPersonProfile().getFirstName() : null, seller.getNaturalPersonProfile() != null
                            ? seller.getNaturalPersonProfile().getLastName() : null) : null,
                    buyer != null ? new UserDto(buyer.getId(), buyer.getMobileNumber(), buyer.getNaturalPersonProfile() != null
                            ? buyer.getNaturalPersonProfile().getFirstName() : null, buyer.getNaturalPersonProfile() != null
                            ? buyer.getNaturalPersonProfile().getLastName() : null) : null,
                    transfer.getBuyerMobileNumber(),
                    transfer.getStatus(),
                    transfer.getCreatedAt(),
                    transfer.getUpdatedAt(),
                    buyerIsYou,
                    sellerIsYou
            );
        }

    }

    public record ReportIssue(ReportIssueStatus status, ReportIssueType type,
                              @FriendlyDate LocalDateTime reportAt) {
    }
}
