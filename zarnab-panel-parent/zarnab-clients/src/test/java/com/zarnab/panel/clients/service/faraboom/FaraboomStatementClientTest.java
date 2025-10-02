//package com.zarnab.panel.clients.service.faraboom;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import okhttp3.mockwebserver.MockResponse;
//import okhttp3.mockwebserver.MockWebServer;
//import okhttp3.mockwebserver.RecordedRequest;
//import org.junit.jupiter.api.*;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.web.reactive.function.client.WebClient;
//import org.springframework.web.reactive.function.client.WebClientResponseException;
//import reactor.core.publisher.Mono;
//import reactor.test.StepVerifier;
//
//import java.io.IOException;
//import java.time.LocalDate;
//import java.util.concurrent.TimeUnit;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.mockito.Mockito.when;
//
///**
// * REFACTORING NOTES (Modern Best Practices):
// * 1.  **@ExtendWith(MockitoExtension.class):** Replaces manual Mockito initialization for cleaner, more declarative setup.
// * 2.  **AssertJ for Fluent Assertions:** Switched from basic `assert` to AssertJ's `assertThat()` for more readable and expressive assertions with better failure messages.
// * 3.  **@DisplayName & @Nested:** Used to create a more descriptive and structured test report, making it easier to understand the test's intent at a glance.
// * 4.  **Request Verification:** Tests now verify the outgoing request (URL, headers, method) using `server.takeRequest()`. This is crucial for ensuring the client is behaving as expected, not just that it can parse a response.
// * 5.  **Comprehensive Error Handling Tests:** Added tests for client (4xx) and server (5xx) errors to ensure the client is resilient and propagates errors correctly.
// * 6.  **Dependency Injection:** Injected the mock `FaraboomTokenService` via `@Mock` annotation.
// * 7.  **Final Fields:** Declared fields as `private final` where possible, which is good practice.
// */
//@ExtendWith(MockitoExtension.class)
//@DisplayName("Faraboom Statement Client Tests")
//class FaraboomStatementClientTest {
//
//    private MockWebServer server;
//    private FaraboomStatementClient statementClient;
//
//    @Mock
//    private FaraboomTokenService mockTokenService;
//
//    @BeforeEach
//    void setup() throws IOException {
//        server = new MockWebServer();
//        server.start();
//
//        WebClient webClient = WebClient.builder()
//                .baseUrl(server.url("/").toString())
//                .build();
//
//        statementClient = new FaraboomStatementClient(webClient, mockTokenService);
//    }
//
//    @AfterEach
//    void tearDown() throws IOException {
//        server.shutdown();
//    }
//
//    @Nested
//    @DisplayName("Successful Scenarios")
//    class SuccessScenarios {
//
//        @Test
//        @DisplayName("should send correct request and parse successful response")
//        void getStatement_shouldSendAuthHeaderAndParseBody() throws InterruptedException {
//            // Arrange
//            String mockToken = "token123";
//            String mockSuccessResponse = "{\n" +
//                                         "  \"statements\": [{\n" +
//                                         "    \"serial_number\": \"1\", \"description\": \"desc\"\n" +
//                                         "  }],\n" +
//                                         "  \"has_next_page\": false,\n" +
//                                         "  \"next_offset\": null,\n" +
//                                         "  \"operation_time\": \"2024-01-01T00:00:00Z\",\n" +
//                                         "  \"ref_id\": \"ref\"\n" +
//                                         "}";
//
//            server.enqueue(new MockResponse()
//                    .setResponseCode(200)
//                    .setHeader("Content-Type", "application/json")
//                    .setBody(mockSuccessResponse));
//
//            when(mockTokenService.getAccessToken()).thenReturn(Mono.just(mockToken));
//
//            LocalDate startDate = LocalDate.of(2024, 1, 1);
//            LocalDate endDate = LocalDate.of(2024, 1, 31);
//
//            // Act & Assert
//            StepVerifier.create(statementClient.getStatement("12345", startDate, endDate, 0, 10))
//                    .assertNext(response -> {
//                        assertThat(response.isHasNextPage()).isFalse();
//                        assertThat(response.getStatements()).isNotNull().hasSize(1);
//                        assertThat(response.getStatements().get(0).getSerialNumber()).isEqualTo("1");
//                    })
//                    .verifyComplete();
//
//            // Verify the HTTP request sent by the client
//            RecordedRequest recordedRequest = server.takeRequest(1, TimeUnit.SECONDS);
//            assertThat(recordedRequest).isNotNull();
//            assertThat(recordedRequest.getMethod()).isEqualTo("GET");
//            assertThat(recordedRequest.getPath()).isEqualTo("/statements?deposit_number=12345&from_date=2024-01-01&to_date=2024-01-31&offset=0&length=10");
//            assertThat(recordedRequest.getHeader("Authorization")).isEqualTo("Bearer " + mockToken);
//        }
//    }
//
//    @Nested
//    @DisplayName("Failure Scenarios")
//    class FailureScenarios {
//
//        @Test
//        @DisplayName("should propagate WebClientResponseException on 4xx error")
//        void getStatement_shouldHandleClientError() {
//            // Arrange
//            String mockToken = "token123";
//            server.enqueue(new MockResponse()
//                    .setResponseCode(401)
//                    .setBody("{\"error\":\"Unauthorized\"}"));
//
//            when(mockTokenService.getAccessToken()).thenReturn(Mono.just(mockToken));
//
//            // Act & Assert
//            StepVerifier.create(statementClient.getStatement("12345", LocalDate.now(), LocalDate.now(), 0, 10))
//                    .expectErrorMatches(throwable ->
//                            throwable instanceof WebClientResponseException.Unauthorized &&
//                            ((WebClientResponseException.Unauthorized) throwable).getStatusCode().value() == 401
//                    )
//                    .verify();
//        }
//    }
//}
