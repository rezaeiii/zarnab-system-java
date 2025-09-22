package com.zarnab.panel.clients.service.faraboom;

import com.zarnab.panel.clients.dto.faraboom.FaraboomBalanceResponse;
import com.zarnab.panel.clients.dto.faraboom.FaraboomTransferRequest;
import com.zarnab.panel.clients.dto.faraboom.FaraboomTransferResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class FaraboomTransferClient {

    private final WebClient faraboomWebClient;
    private final FaraboomTokenService tokenService;

    public FaraboomTransferClient(@Qualifier("faraboomWebClient") WebClient faraboomWebClient, FaraboomTokenService tokenService) {
        this.faraboomWebClient = faraboomWebClient;
        this.tokenService = tokenService;
    }

    /**
     * Performs an internal fund transfer between two deposit accounts.
     * This is a POST request and is NOT idempotent. Our global filter will correctly
     * prevent this request from being retried on server/network errors to avoid
     * duplicate transfers.
     *
     * @param requestDto The data transfer object containing all transfer details.
     * @return A Mono emitting the transfer response from the server.
     */
    public Mono<FaraboomTransferResponse> transfer(FaraboomTransferRequest requestDto) {
        return tokenService.getAccessToken()
                .flatMap(token -> this.faraboomWebClient.post()
                        .uri("/v1/deposits/transfer/normal")
                        .header("Authorization", "Bearer " + token)
                        .bodyValue(requestDto)
                        .retrieve()
                        .bodyToMono(FaraboomTransferResponse.class)
                );
    }

    public Mono<FaraboomTransferResponse> transfer2(FaraboomTransferRequest requestDto) {
        return tokenService.getAccessToken()
                .flatMap(token -> this.faraboomWebClient.post()
                        .uri("/v1/deposits/transfer/normal")
                        .header("Authorization", "Bearer " + token)
                        .header("App-Key", "14383")
                        .header("Device-Id", "192.168.1.1")
                        .header("Bank-Id", "SINAIR")
                        .header("Token-Id", "xxxxxxx")
                        .header("CLIENT-IP-ADDRESS", "127.0.0.1")
                        .header("CLIENT-USER-ID", "0911262456")
                        .header("trace-no", UUID.randomUUID().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(requestDto)
                        .retrieve()
                        .bodyToMono(FaraboomTransferResponse.class)
                );
    }

}