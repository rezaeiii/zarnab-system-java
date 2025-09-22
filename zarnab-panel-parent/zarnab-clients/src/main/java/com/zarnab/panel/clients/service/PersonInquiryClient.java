package com.zarnab.panel.clients.service;

import com.zarnab.panel.clients.config.ClientsConfig;
import com.zarnab.panel.clients.dto.PersonInquiryRequest;
import com.zarnab.panel.clients.dto.PersonInquiryResponse;
import com.zarnab.panel.clients.dto.common.ApiInfo;
import com.zarnab.panel.clients.dto.common.RequestContext;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class PersonInquiryClient {

    private final WebClient uidWebClient;
    private final ClientsConfig properties;

    public PersonInquiryClient(@Qualifier("uidWebClient") WebClient uidWebClient,
                               ClientsConfig properties) {
        this.uidWebClient = uidWebClient;
        this.properties = properties;
    }

    /**
     * Retrieves personal information based on National ID and birthdate.
     * This is a POST request, but for inquiry, it's idempotent in practice.
     * If the server supported it, we would ideally use GET.
     * The global filter will NOT retry this POST by default, which is safe.
     */
    public Mono<PersonInquiryResponse> getPersonInfo(String nationalId, String birthDate) {
        var apiInfo = new ApiInfo(properties.uid().businessId(), properties.uid().businessToken());
        var requestContext = new RequestContext(apiInfo);
        var request = new PersonInquiryRequest(requestContext, nationalId, birthDate);

        return uidWebClient.post()
                .uri("/inquiry/person/v2")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(PersonInquiryResponse.class);
    }
}