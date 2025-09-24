package com.zarnab.panel.ingot.dto;

import com.zarnab.panel.auth.model.User;
import com.zarnab.panel.ingot.model.Ingot;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

public class IngotDtos {

    public record IngotCreateRequest(
            @Schema(description = "Unique serial", requiredMode = Schema.RequiredMode.REQUIRED)
            String serial,
            @Schema(description = "Manufacture date (YYYY-MM-DD)")
            LocalDate manufactureDate,
            @Schema(description = "Karat (purity)")
            Integer karat,
            @Schema(description = "Weight in grams")
            Double weightGrams,
            @Schema(description = "Owner userId (only admin can set)")
            Long ownerId
    ) {
    }

    public record IngotResponse(
            Long id,
            String serial,
            LocalDate manufactureDate,
            Integer karat,
            Double weightGrams,
            UserDto owner
    ) {
        public static IngotResponse from(Ingot ingot) {
            User user = ingot.getOwner();
            String firstName = user.getNaturalPersonProfile() != null ? user.getNaturalPersonProfile().getFirstName() : null;
            String lastName = user.getNaturalPersonProfile() != null ? user.getNaturalPersonProfile().getLastName() : null;
            return new IngotResponse(
                    ingot.getId(),
                    ingot.getSerial(),
                    ingot.getManufactureDate(),
                    ingot.getKarat(),
                    ingot.getWeightGrams(),
                    new UserDto(ingot.getOwner().getId(), ingot.getOwner().getMobileNumber(), firstName, lastName)
            );
        }
    }

    public record UserDto(Long id,
                          String mobile,
                          String firstName,
                          String lastName) {
    }
} 