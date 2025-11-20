package com.zarnab.panel.ingot.dto.res;

import com.zarnab.panel.ingot.model.TransferStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VerifyTransferResponse {
    private TransferStatus status;
}
