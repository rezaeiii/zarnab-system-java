package com.zarnab.panel.clients.service.faraboom;

import com.zarnab.panel.clients.dto.faraboom.FaraboomBalanceRequest;
import com.zarnab.panel.clients.dto.faraboom.FaraboomBalanceResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class FaraboomBalanceClient {

    private final WebClient faraboomWebClient;
    private final FaraboomTokenService tokenService;

    public FaraboomBalanceClient(@Qualifier("faraboomWebClient") WebClient faraboomWebClient, FaraboomTokenService tokenService) {
        this.faraboomWebClient = faraboomWebClient;
        this.tokenService = tokenService;
    }

    /**
     * Retrieves the balance for a given deposit account number.
     * This is a GET request and is idempotent.
     *
     * @param depositNumber The deposit account number to get the balance for.
     * @return A Mono emitting the balance response from the server.
     */
    public Mono<FaraboomBalanceResponse> balance(String depositNumber) {
        return tokenService.getAccessToken()
                .flatMap(token -> this.faraboomWebClient.post()
                        .uri("/v1/deposits/balance")
                        .header("Authorization", "Bearer " + token)
                        .bodyValue(new FaraboomBalanceRequest(depositNumber))
                        .retrieve()
                        .bodyToMono(FaraboomBalanceResponse.class)
                );
    }
}