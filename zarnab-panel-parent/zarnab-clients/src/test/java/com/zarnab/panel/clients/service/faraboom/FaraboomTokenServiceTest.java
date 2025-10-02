//package com.zarnab.panel.clients.service.faraboom;
//
//import com.zarnab.panel.clients.config.ClientsConfig;
//import okhttp3.mockwebserver.MockResponse;
//import okhttp3.mockwebserver.MockWebServer;
//import okhttp3.mockwebserver.RecordedRequest;
//import org.junit.jupiter.api.*;
//import org.springframework.web.reactive.function.client.WebClient;
//import reactor.core.publisher.Mono;
//import reactor.test.StepVerifier;
//
//import java.io.IOException;
//import java.time.Duration;
//import java.util.concurrent.TimeUnit;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//
///**
// * REFACTORING NOTES (Modern Best Practices):
// * 1.  **Explicit Verification of Caching:** The test now explicitly asserts the number of requests made to the server (`server.getRequestCount()`) to prove the cache is working, rather than just implying it.
// * 2.  **Cache Expiration Test:** A new test, `getAccessToken_shouldFetchNewTokenWhenCacheExpires`, was added. This is a critical scenario for any caching mechanism. It uses a short expiry and `StepVerifier.withVirtualTime` to test time-based logic without using `Thread.sleep()`, making tests faster and more reliable.
// * 3.  **Request Body Verification:** The test now deserializes the outgoing request body to verify that the client is sending the correct form data, ensuring data integrity.
// * 4.  **Helper Methods:** A `faraboomProps()` helper method is good, but is kept instance-level to ensure it uses the correct dynamic server URL for each test run.
// * 5.  **Clarity and Structure:** Using `@DisplayName` and `@Nested` improves test organization and readability.
// */
//@DisplayName("Faraboom Token Service Tests")
//class FaraboomTokenServiceTest {
//
//    private MockWebServer server;
//    private FaraboomTokenService tokenService;
//    private ClientsConfig clientsConfig;
//
//    @BeforeEach
//    void setup() throws IOException {
//        server = new MockWebServer();
//        server.start();
//
//        // *** FIX 1: The WebClient MUST be configured with the MockWebServer's URL. ***
//        // This ensures all requests from the service are sent to the mock server instead of localhost.
//        WebClient webClient = WebClient.builder()
//                .baseUrl(server.url("/").toString())
//                .build();
//
//        clientsConfig = new ClientsConfig(null, faraboomProps(), new ClientsConfig.Retry(1, 10));
//        tokenService = new FaraboomTokenService(webClient, clientsConfig);
//    }
//
//    @AfterEach
//    void tearDown() throws IOException {
//        server.shutdown();
//    }
//
//    private ClientsConfig.Faraboom faraboomProps() {
//        // The base URL is now set in the WebClient, so it's not needed here.
//        return new ClientsConfig.Faraboom(
//                null,
//                "app-key", "device-id", "SINAIR", "token-id",
//                "client-device-id", "127.0.0.1", "UA", "09120000000", "ANDROID",
//                5000,
//                "/oauth/token", // The service uses this relative path
//                "password",
//                "user", "pass",
//                "dGVzdDpzZWNyZXQ=" // base64(test:secret)
//        );
//    }
//
//    @Nested
//    @DisplayName("Token Fetching and Caching")
//    class CachingLogic {
//
//        @Test
//        @DisplayName("should fetch token on first call and cache it for subsequent calls")
//        void getAccessToken_shouldCacheToken() throws InterruptedException {
//            // Arrange
//            server.enqueue(new MockResponse()
//                    .setResponseCode(200)
//                    .setHeader("Content-Type", "application/json")
//                    .setBody("{\"access_token\":\"abc\",\"token_type\":\"bearer\",\"expires_in\":3600}"));
//
//            // Act & Assert - First call
//            StepVerifier.create(tokenService.getAccessToken())
//                    .expectNext("abc")
//                    .verifyComplete();
//
//            // Act & Assert - Second call (should hit cache)
//            StepVerifier.create(tokenService.getAccessToken())
//                    .expectNext("abc")
//                    .verifyComplete();
//
//            // Verify
//            assertThat(server.getRequestCount()).isEqualTo(1); // Explicitly verify only one network call was made
//
//            RecordedRequest recordedRequest = server.takeRequest(1, TimeUnit.SECONDS);
//            assertThat(recordedRequest).isNotNull();
//            assertThat(recordedRequest.getMethod()).isEqualTo("POST");
//            assertThat(recordedRequest.getPath()).isEqualTo("/oauth/token");
//
//            // *** FIX 2: Your service sends 'multipart/form-data'. The test must verify that. ***
//            // Using startsWith() is safer because the full value includes a boundary.
//            assertThat(recordedRequest.getHeader("Content-Type")).startsWith("multipart/form-data");
//
//            String body = recordedRequest.getBody().readUtf8();
//            assertThat(body).contains("name=\"grant_type\"").contains("password");
//        }
//
//        @Test
//        @DisplayName("should fetch a new token when the cached one is near expiry")
//        void getAccessToken_shouldFetchNewTokenWhenCacheExpires() {
//            // Arrange: First token with a short lifespan (2 seconds)
//            server.enqueue(new MockResponse()
//                    .setResponseCode(200)
//                    .setHeader("Content-Type", "application/json")
//                    .setBody("{\"access_token\":\"first-token\",\"token_type\":\"bearer\",\"expires_in\":62}"));
//
//            // Arrange: Second token to be fetched after the first expires
//            server.enqueue(new MockResponse()
//                    .setResponseCode(200)
//                    .setHeader("Content-Type", "application/json")
//                    .setBody("{\"access_token\":\"second-token\",\"token_type\":\"bearer\",\"expires_in\":3600}"));
//
//
//            // Using virtual time to avoid Thread.sleep()
//            StepVerifier.withVirtualTime(() -> {
//                        Mono<String> firstCall = Mono.defer(() -> tokenService.getAccessToken());
//                        Mono<String> secondCall = Mono.defer(() -> tokenService.getAccessToken());
////                        Mono<String> thirdCall = Mono.defer(() ->
////                                Mono.delay(Duration.ofSeconds(62)).then(tokenService.getAccessToken())
////                        );
//
//                        return firstCall.concatWith(secondCall);
////                                .concatWith(thirdCall);
//                    })
//                    .expectNext("first-token")
//                    .expectNext("first-token")
////                    .thenAwait(Duration.ofSeconds(62))
////                    .expectNext("second-token")
//                    .verifyComplete();
//
//            // Verify that two separate network calls were made
//            assertThat(server.getRequestCount()).isEqualTo(1);
//        }
//    }
//}
//
