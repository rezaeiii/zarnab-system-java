package com.zarnab.panel.clients.sms;

import com.zarnab.panel.clients.config.ClientsConfig;
import com.zarnab.panel.clients.sms.dto.SmsSendRequest;
import com.zarnab.panel.clients.sms.dto.SmsSendResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Service
public class SmsServiceClientImpl implements SmsServiceClient {

    private final WebClient smsWebClient;
    private final ClientsConfig properties;

    public SmsServiceClientImpl(@Qualifier("smsWebClient") WebClient smsWebClient, ClientsConfig properties) {
        this.smsWebClient = smsWebClient;
        this.properties = properties;
    }

    @Override
    public Mono<SmsSendResponse> send(SmsSendRequest request) {
        return smsWebClient.post()
                .uri("/send/likeToLike")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(SmsSendResponse.class)
                .doOnError(e -> log.error("Error during sending sms: {}", e.getMessage()));
    }

    @Override
    public Mono<SmsSendResponse> send(String mobile, String message) {
        SmsSendRequest request = new SmsSendRequest(
                properties.sms().lineNumber(),
                List.of(message),
                List.of(mobile),
                null
        );
        return send(request);
    }
}
