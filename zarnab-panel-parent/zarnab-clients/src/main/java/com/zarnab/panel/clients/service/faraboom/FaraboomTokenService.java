package com.zarnab.panel.clients.service.faraboom;

import com.zarnab.panel.clients.config.ClientsConfig;
import com.zarnab.panel.clients.dto.faraboom.FaraboomTokenResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Service
public class FaraboomTokenService {

    private final WebClient faraboomWebClient;
    private final ClientsConfig clientsConfig;

    private static final long SAFETY_SECONDS = 60;

    public FaraboomTokenService(@Qualifier("faraboomWebClient") WebClient faraboomWebClient, ClientsConfig clientsConfig) {
        this.faraboomWebClient = faraboomWebClient;
        this.clientsConfig = clientsConfig;
    }

    private record CachedToken(String value, Instant expiresAt) {
    }

    private final AtomicReference<CachedToken> cache = new AtomicReference<>();

    public Mono<String> getAccessToken() {
        CachedToken current = cache.get();
        if (current != null && Instant.now().isBefore(current.expiresAt.minusSeconds(SAFETY_SECONDS))) {
            log.debug("Get faraboom access token from cache {}", current.value);
            return Mono.just(current.value);
        }
        synchronized (this) {
            current = cache.get();
            if (current != null && Instant.now().isBefore(current.expiresAt.minusSeconds(SAFETY_SECONDS))) {
                return Mono.just(current.value);
            }
            return requestNewToken()
                    .map(token -> {
                        cache.set(token);
                        return token.value;
                    });
        }
    }

    private Mono<CachedToken> requestNewToken() {
        ClientsConfig.Faraboom fb = clientsConfig.faraboom();

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", fb.oauthGrantType());
        form.add("username", fb.oauthUsername());
        form.add("password", fb.oauthPassword());

        return faraboomWebClient.post()
                .uri(fb.oauthTokenPath())
                .header(HttpHeaders.AUTHORIZATION, "Basic " + getBase64Credentials(fb))
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(form))
                .retrieve()
                .bodyToMono(FaraboomTokenResponse.class)
                .map(resp -> {
                    String accessToken = Objects.requireNonNull(resp.getAccessToken(), "access_token is null");
                    long expiresIn = resp.getExpiresIn();
                    Instant expiresAt = Instant.now().plusSeconds(expiresIn);
                    log.debug("Fetch new faraboom access token {} expires at {}", accessToken, expiresAt);
                    return new CachedToken(accessToken, expiresAt);
                });
    }

    private String getBase64Credentials(ClientsConfig.Faraboom fb) {
        String credentials = fb.appKey() + ":" + fb.appSecret();
        return Base64.getEncoder()
                .encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
    }

    public void clearCache() {
        cache.set(null);
    }
}
