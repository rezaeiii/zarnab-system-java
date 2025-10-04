package com.zarnab.panel.ingot.dto.res;

import com.zarnab.panel.auth.dto.UserManagementDtos;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InitiateTransferResponse {
    private Long transferId;
    private UserManagementDtos.UserResponse buyer;

}
