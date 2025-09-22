package com.zarnab.panel.ingot.dto;

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
	) {}

	public record IngotResponse(
			Long id,
			String serial,
			LocalDate manufactureDate,
			Integer karat,
			Double weightGrams,
			Long ownerId
	) {
		public static IngotResponse from(Ingot ingot) {
			return new IngotResponse(
					ingot.getId(),
					ingot.getSerial(),
					ingot.getManufactureDate(),
					ingot.getKarat(),
					ingot.getWeightGrams(),
					ingot.getOwner() != null ? ingot.getOwner().getId() : null
			);
		}
	}
} 