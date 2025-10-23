package com.zarnab.panel.ingot.controller;

import com.zarnab.panel.auth.model.User;
import com.zarnab.panel.core.annotations.PageableParam;
import com.zarnab.panel.core.dto.req.PageableRequest;
import com.zarnab.panel.core.dto.res.PageableResponse;
import com.zarnab.panel.ingot.dto.IngotDtos;
import com.zarnab.panel.ingot.dto.req.InitiateTransferRequest;
import com.zarnab.panel.ingot.dto.req.VerifyTransferRequest;
import com.zarnab.panel.ingot.dto.res.InitiateTransferResponse;
import com.zarnab.panel.ingot.service.TransferService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/api/transfers")
@RequiredArgsConstructor
public class TransferController {

    private final TransferService transferService;

    @PostMapping("/initiate")
    public ResponseEntity<InitiateTransferResponse> initiateTransfer(@Valid @RequestBody InitiateTransferRequest request, @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(transferService.initiateTransfer(request, user.getMobileNumber()));
    }

    @PostMapping("/verify")
    public ResponseEntity<IngotDtos.TransferDto> verifyTransfer(@Valid @RequestBody VerifyTransferRequest request, @AuthenticationPrincipal User user) {
        IngotDtos.TransferDto transferDto = transferService.verifyTransfer(request, user.getMobileNumber());
        return ResponseEntity.ok(transferDto);
    }

    @PostMapping("/{transferId}/cancel")
    public ResponseEntity<Void> cancelTransfer(@PathVariable Long transferId, @AuthenticationPrincipal User user) {
        transferService.cancelTransfer(transferId, user.getMobileNumber());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "List transfers with pagination, filtering, and sorting")
    @GetMapping
    public ResponseEntity<PageableResponse<IngotDtos.TransferDto>> getTransfers(
            @AuthenticationPrincipal User user,
            @PageableParam @ParameterObject PageableRequest pageableRequest) {
        return ResponseEntity.ok(transferService.getTransfers(user, pageableRequest));
    }
}
