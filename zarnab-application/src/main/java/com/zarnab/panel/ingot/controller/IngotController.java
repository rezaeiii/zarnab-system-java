package com.zarnab.panel.ingot.controller;

import com.zarnab.panel.auth.model.User;
import com.zarnab.panel.ingot.dto.IngotDtos.IngotCreateRequest;
import com.zarnab.panel.ingot.dto.IngotDtos.IngotResponse;
import com.zarnab.panel.ingot.service.IngotService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/ingots")
@RequiredArgsConstructor
public class IngotController {

	private final IngotService ingotService;

	@GetMapping
	public ResponseEntity<List<IngotResponse>> list(@AuthenticationPrincipal User user) {
		return ResponseEntity.ok(ingotService.list(user));
	}

	@PostMapping
	// TODO uncomment
//	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<IngotResponse> create(@RequestBody IngotCreateRequest request) {
		return ResponseEntity.ok(ingotService.create(request));
	}

	@GetMapping("/inquiry/{serial}")
	public ResponseEntity<IngotResponse> inquiry(@PathVariable String serial) {
		return ResponseEntity.ok(ingotService.inquiry(serial));
	}
}
