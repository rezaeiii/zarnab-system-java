package com.zarnab.panel.ingot.service;

import com.zarnab.panel.ingot.dto.IngotDtos;
import com.zarnab.panel.ingot.dto.req.InitiateTransferRequest;
import com.zarnab.panel.ingot.dto.req.VerifyTransferRequest;

import java.util.List;

public interface TransferService {
    void initiateTransfer(InitiateTransferRequest request, String username);
    void verifyAndCompleteTransfer(VerifyTransferRequest request, String username);
    List<IngotDtos.TransferDto> getTransfers(String username);
}
