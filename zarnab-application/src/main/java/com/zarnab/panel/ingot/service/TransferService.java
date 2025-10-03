package com.zarnab.panel.ingot.service;

import com.zarnab.panel.ingot.dto.IngotDtos;
import com.zarnab.panel.ingot.dto.req.InitiateTransferRequest;
import com.zarnab.panel.ingot.dto.req.VerifyTransferRequest;

import java.util.List;

public interface TransferService {
    Long initiateTransfer(InitiateTransferRequest request, String username);

    IngotDtos.TransferDto verifyTransfer(VerifyTransferRequest request, String username);

    void cancelTransfer(Long transferId, String username);

    List<IngotDtos.TransferDto> getTransfers(String username);
}
