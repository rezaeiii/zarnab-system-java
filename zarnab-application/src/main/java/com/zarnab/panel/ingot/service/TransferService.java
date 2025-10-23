package com.zarnab.panel.ingot.service;

import com.zarnab.panel.auth.model.User;
import com.zarnab.panel.core.dto.req.PageableRequest;
import com.zarnab.panel.core.dto.res.PageableResponse;
import com.zarnab.panel.ingot.dto.IngotDtos;
import com.zarnab.panel.ingot.dto.req.InitiateTransferRequest;
import com.zarnab.panel.ingot.dto.req.VerifyTransferRequest;
import com.zarnab.panel.ingot.dto.res.InitiateTransferResponse;

public interface TransferService {
    InitiateTransferResponse initiateTransfer(InitiateTransferRequest request, String username);

    IngotDtos.TransferDto verifyTransfer(VerifyTransferRequest request, String username);

    void cancelTransfer(Long transferId, String username);

    PageableResponse<IngotDtos.TransferDto> getTransfers(User user, PageableRequest pageableRequest);
}
