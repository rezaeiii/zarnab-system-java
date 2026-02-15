package com.zarnab.panel.ingot.controller;

import com.zarnab.panel.auth.model.User;
import com.zarnab.panel.core.annotations.PageableParam;
import com.zarnab.panel.core.dto.req.PageableRequest;
import com.zarnab.panel.core.dto.res.PageableResponse;
import com.zarnab.panel.ingot.dto.IngotDtos;
import com.zarnab.panel.ingot.dto.IngotDtos.IngotCreateRequest;
import com.zarnab.panel.ingot.dto.IngotDtos.IngotResponse;
import com.zarnab.panel.ingot.dto.req.BatchCreateRequest;
import com.zarnab.panel.ingot.dto.req.BulkIngotStateChangeRequest;
import com.zarnab.panel.ingot.dto.res.BatchCreateResponse;
import com.zarnab.panel.ingot.dto.res.BatchIngotResponse;
import com.zarnab.panel.ingot.service.IngotService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
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

    @Operation(summary = "List ingots with pagination, filtering, and sorting example size=2&page=0&filters=serial:LIKE:DA2&filters=karat:EQUAL:995&sorts=manufactureDate:DESC")
    @GetMapping
    public ResponseEntity<PageableResponse<IngotResponse>> list(
            @AuthenticationPrincipal User user,
            @ParameterObject @PageableParam PageableRequest pageableRequest) {
        return ResponseEntity.ok(ingotService.list(user, pageableRequest));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<IngotResponse> create(@Valid @RequestBody IngotCreateRequest request) {
        return ResponseEntity.ok(ingotService.create(request));
    }

    @GetMapping("/inquiry/{serial}")
    public ResponseEntity<IngotResponse> inquiry(@PathVariable String serial) {
        return ResponseEntity.ok(ingotService.inquiry(serial));
    }

    @Operation(summary = "List ingot batches with pagination, filtering, and sorting")
    @GetMapping("/batch")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PageableResponse<IngotDtos.IngotBatchResponse>> listBatches(
            @ParameterObject @PageableParam PageableRequest pageableRequest) {
        return ResponseEntity.ok(ingotService.listBatches(pageableRequest));
    }

    @PostMapping("/batch")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BatchCreateResponse> createBatch(@Valid @RequestBody BatchCreateRequest request) {
        return ResponseEntity.ok(ingotService.createBatch(request));
    }

    @GetMapping("/batch/{batchId}/csv")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> getBatchCsv(@PathVariable Long batchId, HttpServletRequest request) {
        String baseUrl = "https" + "://" + request.getServerName();
        String csv = ingotService.getBatchCsv(batchId, baseUrl);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=ingots-" + batchId + ".csv");
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN_VALUE);
        return ResponseEntity.ok().headers(headers).body(csv);
    }

    @GetMapping("/batch/{batchId}/ingots")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<BatchIngotResponse>> getBatchIngots(@PathVariable Long batchId) {
        return ResponseEntity.ok(ingotService.getBatchIngots(batchId));
    }

    @PutMapping("/{serial}/assign")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<IngotResponse> assignIngot(@PathVariable String serial) {
        return ResponseEntity.ok(ingotService.assignIngot(serial));
    }

    @PutMapping("/{serial}/unassign")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<IngotResponse> unassignIngot(@PathVariable String serial) {
        return ResponseEntity.ok(ingotService.unassignIngot(serial));
    }

    @PutMapping("/bulk-state-change")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> bulkStateChange(@Valid @RequestBody BulkIngotStateChangeRequest request) {
        ingotService.bulkStateChange(request);
        return ResponseEntity.ok().build();
    }
}
