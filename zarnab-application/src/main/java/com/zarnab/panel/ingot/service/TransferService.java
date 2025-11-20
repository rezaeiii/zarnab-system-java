package com.zarnab.panel.ingot.service;

import com.zarnab.panel.auth.model.User;
import com.zarnab.panel.core.dto.req.PageableRequest;
import com.zarnab.panel.core.dto.res.PageableResponse;
import com.zarnab.panel.ingot.dto.IngotDtos;
import com.zarnab.panel.ingot.dto.req.InitiateQuickTransferRequest;
import com.zarnab.panel.ingot.dto.req.InitiateTransferRequest;
import com.zarnab.panel.ingot.dto.req.SetReceiverRequest;
import com.zarnab.panel.ingot.dto.req.VerifyTransferRequest;
import com.zarnab.panel.ingot.dto.res.InitiateQuickTransferResponse;
import com.zarnab.panel.ingot.dto.res.InitiateTransferResponse;
import com.zarnab.panel.ingot.dto.res.VerifyTransferResponse;

public interface TransferService {
    InitiateTransferResponse initiateTransfer(InitiateTransferRequest request, User user);

    InitiateQuickTransferResponse initiateQuickTransfer(InitiateQuickTransferRequest request);

    void verifySender(VerifyTransferRequest request);

    void receiverAction(Long transferId, boolean isApproved, User currentUser);

    VerifyTransferResponse setReceiver(SetReceiverRequest request);

    void verifyReceiver(VerifyTransferRequest request);

    void cancelTransfer(Long transferId, String username);

    PageableResponse<IngotDtos.TransferDto> getTransfers(User user, PageableRequest pageableRequest);
}
