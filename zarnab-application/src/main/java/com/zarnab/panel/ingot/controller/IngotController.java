package com.zarnab.panel.ingot.controller;

import com.zarnab.panel.auth.model.User;
import com.zarnab.panel.ingot.dto.IngotDtos.IngotCreateRequest;
import com.zarnab.panel.ingot.dto.IngotDtos.IngotResponse;
import com.zarnab.panel.ingot.dto.req.BatchCreateRequest;
import com.zarnab.panel.ingot.dto.res.BatchCreateResponse;
import com.zarnab.panel.ingot.dto.res.IngotBatchResponse;
import com.zarnab.panel.ingot.service.IngotService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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

	@GetMapping("all")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<List<IngotResponse>> listAll(@AuthenticationPrincipal User user) {
		return ResponseEntity.ok(ingotService.list(user));
	}

	@PostMapping
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<IngotResponse> create(@RequestBody IngotCreateRequest request) {
		return ResponseEntity.ok(ingotService.create(request));
	}

	@GetMapping("/inquiry/{serial}")
	public ResponseEntity<IngotResponse> inquiry(@PathVariable String serial) {
		return ResponseEntity.ok(ingotService.inquiry(serial));
	}

	@GetMapping("/batch")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<IngotBatchResponse>> listBatches() {
        return ResponseEntity.ok(ingotService.listBatches());
    }

	@PostMapping("/batch")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BatchCreateResponse> createBatch(@Valid @RequestBody BatchCreateRequest request) {
        return ResponseEntity.ok(ingotService.createBatch(request));
    }

    @GetMapping("/batch/{batchId}/csv")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> getBatchCsv(@PathVariable Long batchId) {
        String csv = ingotService.getBatchCsv(batchId);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=ingots-" + batchId + ".csv");
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN_VALUE);
        return ResponseEntity.ok().headers(headers).body(csv);
    }

    @PutMapping("/{serial}/assign")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<IngotResponse> assignIngot(@PathVariable String serial) {
        return ResponseEntity.ok(ingotService.assignIngot(serial));
    }
}
