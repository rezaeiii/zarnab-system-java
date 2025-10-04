package com.zarnab.panel.ingot.service;

import com.zarnab.panel.ingot.dto.IngotDtos;
import com.zarnab.panel.ingot.dto.req.InitiateTransferRequest;
import com.zarnab.panel.ingot.dto.req.VerifyTransferRequest;
import com.zarnab.panel.ingot.dto.res.InitiateTransferResponse;

import java.util.List;

public interface TransferService {
    InitiateTransferResponse initiateTransfer(InitiateTransferRequest request, String username);

    IngotDtos.TransferDto verifyTransfer(VerifyTransferRequest request, String username);

    void cancelTransfer(Long transferId, String username);

    List<IngotDtos.TransferDto> getTransfers(String username);
}
