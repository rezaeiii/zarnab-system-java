package com.zarnab.panel.clients.dto;

import com.zarnab.panel.clients.dto.common.RequestContext;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PersonInquiryRequest {
    private RequestContext requestContext;
    private String nationalId;
    private String birthDate;
}
