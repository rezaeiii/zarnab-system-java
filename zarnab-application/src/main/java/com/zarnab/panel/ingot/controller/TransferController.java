package com.zarnab.panel.ingot.controller;

import com.zarnab.panel.auth.model.User;
import com.zarnab.panel.ingot.dto.IngotDtos;
import com.zarnab.panel.ingot.dto.req.InitiateTransferRequest;
import com.zarnab.panel.ingot.dto.req.VerifyTransferRequest;
import com.zarnab.panel.ingot.dto.res.InitiateTransferResponse;
import com.zarnab.panel.ingot.service.TransferService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping
    public ResponseEntity<List<IngotDtos.TransferDto>> getTransfers(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(transferService.getTransfers(user.getMobileNumber()));
    }
}
