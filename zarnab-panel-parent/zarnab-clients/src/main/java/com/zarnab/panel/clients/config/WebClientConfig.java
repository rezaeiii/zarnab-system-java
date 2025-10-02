package com.zarnab.panel.clients.config;

import com.zarnab.panel.clients.exception.ClientApiException;
import io.netty.channel.ChannelOption;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.Set;

@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(ClientsConfig.class)
public class WebClientConfig {

    // Define the set of HTTP methods that are safe to retry automatically
    private static final Set<HttpMethod> IDEMPOTENT_METHODS = Set.of(
            HttpMethod.GET, HttpMethod.PUT, HttpMethod.DELETE,
            HttpMethod.HEAD, HttpMethod.OPTIONS, HttpMethod.TRACE
    );

    @Bean
    public WebClient uidWebClient(ClientsConfig properties) {

        ClientsConfig.Uid uidProps = properties.uid();
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, uidProps.timeoutMs())
                .responseTimeout(Duration.ofMillis(uidProps.timeoutMs()));
//                .doOnConnected(conn -> conn
//                        .addHandlerLast(new ReadTimeoutHandler(uidProps.timeoutMs(), TimeUnit.MILLISECONDS))
//                        .addHandlerLast(new WriteTimeoutHandler(uidProps.timeoutMs(), TimeUnit.MILLISECONDS)));

        return WebClient.builder()
                .baseUrl(uidProps.baseUrl())
//                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .filter(logRequest())
                .filter(logResponse())
//                .filter(createRetryAndErrorHandlingFilter(properties.retry()))
                .build();
    }


    private ExchangeFilterFunction createRetryAndErrorHandlingFilter(ClientsConfig.Retry retryProps) {
        return (request, next) -> {
            boolean isIdempotent = IDEMPOTENT_METHODS.contains(request.method());

            Retry retryStrategy = Retry.backoff(retryProps.maxAttempts(), Duration.ofMillis(retryProps.backoffMs()))
                    .jitter(0.75)
                    .filter(this::isRetryableException)
                    .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) ->
                            new ClientApiException("External API call failed after " + retrySignal.totalRetries() + " retries.")
                    );

            Mono<ClientResponse> responseMono = next.exchange(request)
                    .flatMap(response -> {
                        if (response.statusCode().isError()) {
                            return handleApiError(response);
                        }
                        return Mono.just(response);
                    });

            if (isIdempotent) {
                return responseMono.retryWhen(retryStrategy);
            }

            return responseMono;
        };
    }

    private ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            log.info("Request: {} {}", clientRequest.method(), clientRequest.url());
            clientRequest.headers().forEach((name, values) -> values.forEach(value -> log.info("{}={}", name, value)));
            return Mono.just(clientRequest);
        });
    }

    private ExchangeFilterFunction logResponse() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> clientResponse.bodyToMono(String.class)
                .flatMap(body -> {
                    log.info("Response Body: {}", body);
                    return Mono.just(clientResponse.mutate().body(body).build());
                })
                .switchIfEmpty(Mono.just(clientResponse)));
    }

    /**
     * Helper method to determine if an exception is a candidate for retrying.
     * We retry on 5xx server exceptions and on network exceptions.
     */
    private boolean isRetryableException(Throwable throwable) {
        return throwable instanceof ClientApiException.Client5xxException ||
               throwable instanceof WebClientRequestException;
    }

    private Mono<ClientResponse> handleApiError(ClientResponse response) {
        HttpStatusCode status = response.statusCode();
        return response.bodyToMono(String.class)
                .defaultIfEmpty("Error body is empty")
                .flatMap(errorBody -> {
                    String errorMessage = String.format("API call failed with status %s and body: %s", status, errorBody);

                    if (status.is4xxClientError()) {
                        return Mono.error(new ClientApiException.Client4xxException(errorMessage));
                    }

                    if (status.is5xxServerError()) {
                        return Mono.error(new ClientApiException.Client5xxException(errorMessage));
                    }

                    return Mono.error(new ClientApiException(errorMessage));
                });
    }

}