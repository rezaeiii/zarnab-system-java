package com.zarnab.panel.clients.service;

import com.zarnab.panel.clients.config.ClientsConfig;
import com.zarnab.panel.clients.dto.AddressInquiryRequest;
import com.zarnab.panel.clients.dto.AddressInquiryResponse;
import com.zarnab.panel.clients.dto.common.ApiInfo;
import com.zarnab.panel.clients.dto.common.RequestContext;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class AddressInquiryClient {

    private final WebClient uidWebClient;
    private final ClientsConfig properties;

    public AddressInquiryClient(@Qualifier("uidWebClient") WebClient uidWebClient,
                               ClientsConfig properties) {
        this.uidWebClient = uidWebClient;
        this.properties = properties;
    }

    public Mono<AddressInquiryResponse> getAddressInfo(String postalCode) {
        var apiInfo = new ApiInfo(properties.uid().businessId(), properties.uid().businessToken());
        var requestContext = new RequestContext(apiInfo);
        var request = new AddressInquiryRequest(requestContext, postalCode);

        return uidWebClient.post()
                .uri("/inquiry/address/v2")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(AddressInquiryResponse.class);
    }
}
