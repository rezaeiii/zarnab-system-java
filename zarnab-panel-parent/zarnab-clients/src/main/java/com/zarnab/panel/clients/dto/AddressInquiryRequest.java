package com.zarnab.panel.clients.dto;

import com.zarnab.panel.clients.dto.common.RequestContext;

public record AddressInquiryRequest(RequestContext requestContext, String postalCode) {
}
