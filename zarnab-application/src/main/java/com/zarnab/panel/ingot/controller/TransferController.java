package com.zarnab.panel.ingot.controller;

import com.zarnab.panel.auth.model.User;
import com.zarnab.panel.core.annotations.PageableParam;
import com.zarnab.panel.core.dto.req.PageableRequest;
import com.zarnab.panel.core.dto.res.PageableResponse;
import com.zarnab.panel.ingot.dto.IngotDtos;
import com.zarnab.panel.ingot.dto.req.*;
import com.zarnab.panel.ingot.dto.res.InitiateQuickTransferResponse;
import com.zarnab.panel.ingot.dto.res.InitiateTransferResponse;
import com.zarnab.panel.ingot.dto.res.VerifyTransferResponse;
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
        return ResponseEntity.ok(transferService.initiateTransfer(request, user));
    }

    @PostMapping("/quick-initiate")
    public ResponseEntity<InitiateQuickTransferResponse> initiateQuickTransfer(@Valid @RequestBody InitiateQuickTransferRequest request) {
        return ResponseEntity.ok(transferService.initiateQuickTransfer(request));
    }

    @PostMapping("/verify-sender")
    public ResponseEntity<Void> verifySender(@Valid @RequestBody VerifyTransferRequest request) {
        transferService.verifySender(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/quick-verify-sender")
    public ResponseEntity<Void> verifySenderQuickTransfer(@Valid @RequestBody VerifyQuickTransferRequest request) {
        transferService.verifySenderQuickTransfer(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/set-receiver")
    public ResponseEntity<VerifyTransferResponse> setReceiver(@Valid @RequestBody SetReceiverRequest request) {
        return ResponseEntity.ok(transferService.setReceiver(request));
    }

//    @PostMapping("/verify-receiver")
//    public ResponseEntity<Void> verifyReceiver(@Valid @RequestBody VerifyTransferRequest request) {
//        transferService.verifyReceiver(request);
//        return ResponseEntity.ok().build();
//    }

    @PostMapping("/{transferId}/receiver/approve/{isApproved}")
    public ResponseEntity<Void> receiverAction(@PathVariable Long transferId, @PathVariable boolean isApproved,
                                               @AuthenticationPrincipal User user) {
        transferService.receiverAction(transferId, isApproved, user);
        return ResponseEntity.ok().build();
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
