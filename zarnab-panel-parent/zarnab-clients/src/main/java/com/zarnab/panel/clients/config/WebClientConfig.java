package com.zarnab.panel.clients.config;

import com.zarnab.panel.clients.exception.ClientApiException;
import io.netty.channel.ChannelOption;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import lombok.RequiredArgsConstructor;
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

import javax.net.ssl.SSLException;
import java.time.Duration;
import java.util.Set;

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
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .filter(createRetryAndErrorHandlingFilter(properties.retry()))
                .build();
    }

    /**
     * This is the corrected WebClient @Bean configuration.
     * Place this in a @Configuration class.
     */
    @Bean
    public WebClient faraboomWebClient(ClientsConfig properties) {
        ClientsConfig.Faraboom faraboomProps = properties.faraboom();

        // --- SSL Configuration to trust all certs and disable hostname verification ---
        // This exactly matches the logic from the working main method.
        HttpClient httpClient;
        try {
            httpClient = HttpClient.create()
                    .secure(t -> {
                        try {
                            t.sslContext(SslContextBuilder.forClient()
                                    .trustManager(InsecureTrustManagerFactory.INSTANCE).build());
                        } catch (SSLException e) {
                            // In a real app, you'd likely have a cleaner way to handle this startup failure.
                            throw new RuntimeException("Failed to create insecure SSL context", e);
                        }
                    })
                    // Use responseTimeout for a clear, overall timeout.
                    .responseTimeout(Duration.ofMillis(faraboomProps.timeoutMs()));
        } catch (Exception e) {
            throw new RuntimeException("Failed to build HttpClient", e);
        }

        return WebClient.builder()
                .baseUrl(faraboomProps.baseUrl())
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                // --- Default Headers corrected to match the working main method ---
                .defaultHeader("Accept-Language", "fa")
                .defaultHeader("App-Key", faraboomProps.appKey())
                .defaultHeader("Device-Id", faraboomProps.deviceId())
                .defaultHeader("Bank-Id", faraboomProps.bankId())
                .defaultHeader("Token-Id", faraboomProps.tokenId())
                .defaultHeader("Client-Device-Id", faraboomProps.clientDeviceId())
                .defaultHeader("Client-Ip-Address", faraboomProps.clientIpAddress())
                .defaultHeader("Client-User-Agent", faraboomProps.clientUserAgent())
                .defaultHeader("Client-User-Id", faraboomProps.clientUserId())
                .defaultHeader("Client-Platform-Type", faraboomProps.clientPlatformType())
                // ADDED: This header was missing from your original bean configuration.
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json")
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
//
//
//    public static void main(String[] args) {
//        // --- 1. Corrected Configuration ---
//        String baseUrl = "https://api.sandbox.faraboom.co";
//        String depositNumber = "119-813-2295556-1";
//
//        // !!! IMPORTANT !!!
//        // The 401 UNAUTHORIZED error means your tokens have expired.
//        // You MUST generate new, valid tokens from the Faraboom API developer portal
//        // and replace the placeholder values below.
//        String accessToken = "fae2492e-5c40-491f-9ce7-6d36f792c101"; // Replace this value
//        String tokenId = "N9kQRchc95YCxEILe6Ns7yF89RFVMqa1S0vwDSz4ePTuDFTKPHK7DVo0VK2esv8csnS3CfepiUrQoGXiGaTE";
//
//        // The API uses 'page' and 'size' for pagination
//        int page = 1;
//        int size = 10;
//        int timeoutMs = 30000;
//
//        try {
//            // --- 2. Insecure SSL Context and Hostname Verifier ---
//            // This part is crucial to replicate cURL's --insecure flag completely.
//            // It trusts all certificates AND disables hostname verification.
//            SslContext sslContext = SslContextBuilder.forClient()
//                    .trustManager(InsecureTrustManagerFactory.INSTANCE)
//                    .build();
//
//            // The .secure() provider disables hostname verification by default when a custom trust manager is used.
//            // For clarity, this is how you would build it manually if needed, but it's often implicit.
//            HttpClient httpClient = HttpClient.create()
//                    .secure(t -> t.sslContext(sslContext)) // Apply the insecure SSL context
//                    .responseTimeout(Duration.ofMillis(timeoutMs));
//
//
//            // --- 3. Build WebClient with Corrected Default Headers ---
//            WebClient webClient = WebClient.builder()
//                    .baseUrl(baseUrl)
//                    .clientConnector(new ReactorClientHttpConnector(httpClient))
//                    .defaultHeader("Accept-Language", "fa")
//                    .defaultHeader("App-Key", "14383")
//                    .defaultHeader("Device-Id", "192.168.1.1")
//                    .defaultHeader("Bank-Id", "SINAIR")
//                    .defaultHeader("Token-Id", tokenId)
//                    .defaultHeader("Client-Device-Id", "127.0.0.1")
//                    .defaultHeader("Client-Ip-Address", "127.0.0.1")
//                    // Corrected User-Agent to match cURL
//                    .defaultHeader("Client-User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
//                    // Corrected User-Id to match cURL
//                    .defaultHeader("Client-User-Id", "09120000000")
//                    .defaultHeader("Client-Platform-Type", "WEB")
//                    // Added Content-Type header to match cURL
//                    .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json")
//                    .build();
//
//            // --- 4. Call the API with Corrected URI and Parameters ---
//            System.out.println("Calling API...");
//            String response = webClient.get()
//                    .uri(uriBuilder -> uriBuilder
//                            .path("/v1/deposits/{depositNumber}/statements")
//                            // Use the correct query parameters: page and size
//                            .queryParam("page", page)
//                            .queryParam("size", size)
//                            .build(depositNumber))
//                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
//                    .retrieve()
//                    .bodyToMono(String.class)
//                    .timeout(Duration.ofMillis(timeoutMs))
//                    .block(); // Using .block() for this command-line example is acceptable.
//
//            System.out.println("Response Received:");
//            System.out.println(response);
//
//        } catch (SSLException e) {
//            System.err.println("Error initializing SSL context:");
//            e.printStackTrace();
//        } catch (Exception e) {
//            System.err.println("Error calling API:");
//            e.printStackTrace();
//        }
//    }

//    public static void main(String[] args) {
//        // 1. Correct URL with 'sandbox' and correct query parameters
//        String url = "https://api.sandbox.faraboom.co/v1/deposits/119-813-2295556-1/statements?page=1&size=10";
//
//        // 2. Use the exact tokens from your cURL command
//        String bearerToken = "07d59c6d-5eab-4e0c-85f6-ba98f14bb164";
//        String tokenId = "N9kQRchc95YCxEILe6Ns7yF89RFVMqa1S0vwDSz4ePTuDFTKPHK7DVo0VK2esv8csnS3CfepiUrQoGXiGaTE";
//
//        // This is the standard client. See the section below for an "insecure" version.
//
//        OkHttpClient client = getInsecureOkHttpClient();
//
//        // 3. Build the request with matching headers
//        Request request = new Request.Builder()
//                .url(url)
//                .addHeader("Accept-Language", "fa")
//                .addHeader("Authorization", "Bearer " + bearerToken)
//                .addHeader("App-Key", "14383")
//                .addHeader("Device-Id", "192.168.1.1")
//                .addHeader("Bank-Id", "SINAIR")
//                .addHeader("Token-Id", tokenId)
//                .addHeader("Client-Device-Id", "127.0.0.1")
//                .addHeader("Client-Ip-Address", "127.0.0.1")
//                .addHeader("Client-User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
//                .addHeader("Client-User-Id", "09120000000")
//                .addHeader("Client-Platform-Type", "WEB")
//                .addHeader("Content-Type", "application/json") // Added this header
//                .get()
//                .build();
//
//        try (Response response = client.newCall(request).execute()) {
//            System.out.println("Status: " + response.code());
//            if (response.isSuccessful() && response.body() != null) {
//                System.out.println(response.body().string());
//            } else if (response.body() != null) {
//                System.err.println("Request failed: " + response.body().string());
//            } else {
//                System.err.println("Request failed with empty body.");
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

//    public static OkHttpClient getInsecureOkHttpClient() {
//        try {
//            // Create a trust manager that does not validate certificate chains
//            final TrustManager[] trustAllCerts = new TrustManager[]{
//                    new X509TrustManager() {
//                        @Override
//                        public void checkClientTrusted(X509Certificate[] chain, String authType) {}
//
//                        @Override
//                        public void checkServerTrusted(X509Certificate[] chain, String authType) {}
//
//                        @Override
//                        public X509Certificate[] getAcceptedIssuers() {
//                            return new X509Certificate[]{};
//                        }
//                    }
//            };
//
//            // Install the all-trusting trust manager
//            final SSLContext sslContext = SSLContext.getInstance("SSL");
//            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
//
//            // Create an ssl socket factory with our all-trusting manager
//            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
//
//            OkHttpClient.Builder builder = new OkHttpClient.Builder();
//            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
//
//            // This is the line that fixes your specific problem
//            // It disables the hostname verification check
//            builder.hostnameVerifier((hostname, session) -> true);
//
//            // Add timeouts
//            builder.connectTimeout(30, TimeUnit.SECONDS);
//            builder.readTimeout(30, TimeUnit.SECONDS);
//            builder.writeTimeout(30, TimeUnit.SECONDS);
//
//            return builder.build();
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }

}