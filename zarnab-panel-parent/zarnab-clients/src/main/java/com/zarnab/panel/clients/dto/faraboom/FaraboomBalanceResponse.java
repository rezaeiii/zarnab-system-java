package com.zarnab.panel.clients.dto.faraboom;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public class FaraboomBalanceResponse {

    private BigDecimal balance;
    private String currency;
    private String depositNumber;

}