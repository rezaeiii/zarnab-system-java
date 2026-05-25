package com.zarnab.panel.ingot.service;

import com.zarnab.panel.auth.model.User;
import com.zarnab.panel.core.dto.req.PageableRequest;
import com.zarnab.panel.core.dto.res.PageableResponse;
import com.zarnab.panel.ingot.dto.IngotDtos;
import com.zarnab.panel.ingot.dto.MonthlyWeightDashboard;
import com.zarnab.panel.ingot.dto.req.*;
import com.zarnab.panel.ingot.dto.res.InitiateQuickTransferResponse;
import com.zarnab.panel.ingot.dto.res.InitiateTransferResponse;
import com.zarnab.panel.ingot.dto.res.VerifyTransferResponse;

import java.nio.file.AccessDeniedException;
import java.util.List;

public interface TransferService {
    InitiateTransferResponse initiateTransfer(InitiateTransferRequest request, User user);

    InitiateQuickTransferResponse initiateQuickTransfer(InitiateQuickTransferRequest request);

    void verifySenderQuickTransfer(VerifyQuickTransferRequest request);

    VerifyTransferResponse verifySender(VerifyTransferRequest request);

    void receiverAction(Long transferId, boolean isApproved, User currentUser);

    VerifyTransferResponse setReceiver(SetReceiverRequest request);

    void verifyReceiver(VerifyQuickTransferRequest request);

    void cancelTransfer(Long transferId, String username);

    PageableResponse<IngotDtos.TransferDto> getTransfers(User user, PageableRequest pageableRequest);

    PageableResponse<IngotDtos.TransferDto> getCounterTransfers(User user, PageableRequest pageableRequest);

    List<MonthlyWeightDashboard> getMonthlyCounterToUserTransfers(User user) throws AccessDeniedException;
}
