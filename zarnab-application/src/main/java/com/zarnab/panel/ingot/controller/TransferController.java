package com.zarnab.panel.ingot.controller;

import com.zarnab.panel.auth.model.User;
import com.zarnab.panel.ingot.dto.IngotDtos;
import com.zarnab.panel.ingot.dto.req.InitiateTransferRequest;
import com.zarnab.panel.ingot.dto.req.VerifyTransferRequest;
import com.zarnab.panel.ingot.service.TransferService;
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
    public ResponseEntity<Void> initiateTransfer(@RequestBody InitiateTransferRequest request, @AuthenticationPrincipal User user) {
        transferService.initiateTransfer(request, user.getMobileNumber());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/verify")
    public ResponseEntity<Void> verifyAndCompleteTransfer(@RequestBody VerifyTransferRequest request, @AuthenticationPrincipal User user) {
        transferService.verifyAndCompleteTransfer(request, user.getMobileNumber());
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<IngotDtos.TransferDto>> getTransfers(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(transferService.getTransfers(user.getMobileNumber()));
    }
}
