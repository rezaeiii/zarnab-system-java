package com.zarnab.panel.clients.service;

import com.zarnab.panel.clients.config.ClientsConfig;
import com.zarnab.panel.clients.dto.MobileOwnerInquiryRequest;
import com.zarnab.panel.clients.dto.MobileOwnerInquiryResponse;
import com.zarnab.panel.clients.dto.common.ApiInfo;
import com.zarnab.panel.clients.dto.common.RequestContext;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class ShahkarInquiryClient {

    private final WebClient uidWebClient;
    private final ClientsConfig properties;

    public ShahkarInquiryClient(@Qualifier("uidWebClient") WebClient uidWebClient,
                                ClientsConfig properties) {
        this.uidWebClient = uidWebClient;
        this.properties = properties;
    }

    /**
     * Verifies if a mobile number is registered to a person with the given National ID.
     * As a POST request, the global filter will correctly NOT retry this on failure.
     */
    public Mono<Boolean> verifyMobileOwner(String nationalId, String mobileNumber) {
        var apiInfo = new ApiInfo(properties.uid().businessId(), properties.uid().businessToken());
        var requestContext = new RequestContext(apiInfo);
        var request = new MobileOwnerInquiryRequest(requestContext, nationalId, mobileNumber);

        return uidWebClient.post()
                .uri("/inquiry/mobile/owner/v2")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(MobileOwnerInquiryResponse.class)
                .map(MobileOwnerInquiryResponse::isResult);
    }
}