package com.zarnab.panel.inheritance.controller;

import com.zarnab.panel.core.annotations.PageableParam;
import com.zarnab.panel.core.dto.req.PageableRequest;
import com.zarnab.panel.core.dto.res.PageableResponse;
import com.zarnab.panel.inheritance.dto.InheritanceDtos;
import com.zarnab.panel.inheritance.service.InheritanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/api/admin/inheritance")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminInheritanceController {

    private final InheritanceService inheritanceService;

    @GetMapping
    public ResponseEntity<PageableResponse<InheritanceDtos.CaseResponse>> listCases(@ParameterObject @PageableParam PageableRequest pageableRequest) {
        return ResponseEntity.ok(inheritanceService.listCases(pageableRequest));
    }

    @GetMapping("/{caseId}")
    public ResponseEntity<InheritanceDtos.CaseResponse> getCase(@PathVariable Long caseId) {
        return ResponseEntity.ok(inheritanceService.getCase(caseId));
    }

    @PostMapping("/{caseId}/add-heirs")
    public ResponseEntity<Void> addHeirsToCase(@PathVariable Long caseId, @Valid @RequestBody InheritanceDtos.AddHeirsRequest request) {
        inheritanceService.addHeirsToCase(caseId, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{caseId}/heirs/{heirId}")
    public ResponseEntity<Void> removeHeirFromCase(@PathVariable Long caseId, @PathVariable Long heirId) {
        inheritanceService.removeHeirFromCase(caseId, heirId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{caseId}/status")
    public ResponseEntity<Void> updateCaseStatus(@PathVariable Long caseId, @Valid @RequestBody InheritanceDtos.UpdateCaseStatusRequest request) {
        inheritanceService.updateCaseStatus(caseId, request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{caseId}/heirs/{heirId}/verify-document")
    public ResponseEntity<Void> verifyHeirDocument(@PathVariable Long caseId, @PathVariable Long heirId, @Valid @RequestBody InheritanceDtos.UpdateHeirDocumentStatusRequest request) {
        inheritanceService.verifyHeirDocument(caseId, heirId, request);
        return ResponseEntity.ok().build();
    }
}
