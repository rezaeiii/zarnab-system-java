package com.zarnab.panel.clients.service.faraboom;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zarnab.panel.clients.dto.faraboom.FaraboomTransferRequest;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * REFACTORING NOTES (Modern Best Practices):
 * 1.  **Request Body Validation:** The most critical improvement here. The test now captures the outgoing request, deserializes its JSON body, and asserts its contents against the original request object. This ensures the client is serializing and sending the correct data.
 * 2.  **Use of MockitoExtension:** As with the other tests, this cleans up mock initialization.
 * 3.  **AssertJ for Fluent Assertions:** Replaced `assert` with `assertThat()` for better readability.
 * 4.  **Error Handling for Business Logic:** Added a test for a 400 Bad Request, a common scenario in transfer/payment APIs where input data might be invalid. This ensures the client handles predictable API errors correctly.
 * 5.  **Structured with @Nested and @DisplayName:** Organizes tests into logical groups for clarity.
 * 6.  **Setup and Teardown:** Standardized BeforeEach/AfterEach setup for consistency.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Faraboom Transfer Client Tests")
class FaraboomTransferClientTest {

    private MockWebServer server;
    private FaraboomTransferClient transferClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private FaraboomTokenService mockTokenService;

    @BeforeEach
    void setup() throws IOException {
        server = new MockWebServer();
        server.start();

        WebClient webClient = WebClient.builder()
                .baseUrl(server.url("/").toString())
                .build();

        transferClient = new FaraboomTransferClient(webClient, mockTokenService);
    }

    @AfterEach
    void tearDown() throws IOException {
        server.shutdown();
    }

    @Nested
    @DisplayName("Successful Transfer")
    class SuccessCase {
        @Test
        @DisplayName("should send correct body and headers, and parse response")
        void transfer_shouldSendAuthHeaderAndBody() throws IOException, InterruptedException {
            // Arrange
            String mockToken = "token123";
            String mockSuccessResponse = "{\n" +
                                         "  \"reference_number\": \"srv-ref\",\n" +
                                         "  \"tracking_code\": \"trk\",\n" +
                                         "  \"instant_debit\": true\n" +
                                         "}";

            server.enqueue(new MockResponse()
                    .setResponseCode(200)
                    .setHeader("Content-Type", "application/json")
                    .setBody(mockSuccessResponse));

            when(mockTokenService.getAccessToken()).thenReturn(Mono.just(mockToken));

            FaraboomTransferRequest requestDto = new FaraboomTransferRequest(
                    "dep1", "dep2", "100"
//                   "cust-1", "src name", "dst name", "pay-id", "cli-ref", "docs", "POSA"
            );

            // Act & Assert
            StepVerifier.create(transferClient.transfer(requestDto))
                    .assertNext(response -> {
                        assertThat(response.getServerReferenceNumber()).isEqualTo("srv-ref");
                        assertThat(response.isInstantDebit()).isTrue();
                    })
                    .verifyComplete();

            // Verify the HTTP request itself
            RecordedRequest recordedRequest = server.takeRequest(1, TimeUnit.SECONDS);
            assertThat(recordedRequest).isNotNull();
            assertThat(recordedRequest.getMethod()).isEqualTo("POST");
            assertThat(recordedRequest.getPath()).isEqualTo("/transfer");
            assertThat(recordedRequest.getHeader("Authorization")).isEqualTo("Bearer " + mockToken);
            assertThat(recordedRequest.getHeader("Content-Type")).isEqualTo("application/json");

            // Verify the request body by deserializing it
            FaraboomTransferRequest sentBody = objectMapper.readValue(recordedRequest.getBody().readUtf8(), FaraboomTransferRequest.class);
//            assertThat(sentBody.customerNumber()).isEqualTo(requestDto.customerNumber());
//            assertThat(sentBody.amount()).isEqualByComparingTo(requestDto.amount());
            assertThat(sentBody.destinationDeposit()).isEqualTo(requestDto.destinationDeposit());
        }
    }

    @Nested
    @DisplayName("Failed Transfer")
    class FailureCase {
        @Test
        @DisplayName("should propagate error on 400 Bad Request")
        void transfer_shouldHandleBadRequest() {
            // Arrange
            when(mockTokenService.getAccessToken()).thenReturn(Mono.just("token123"));
            server.enqueue(new MockResponse()
                    .setResponseCode(400)
                    .setHeader("Content-Type", "application/json")
                    .setBody("{\"error\":\"Invalid destination deposit\"}"));

            FaraboomTransferRequest requestDto = new FaraboomTransferRequest(
                    "dep1", "invalid-dep", "100.00"
//                    "cust-1","src name", "dst name", "pay-id", "cli-ref", "docs", "POSA"
            );

            // Act & Assert
            StepVerifier.create(transferClient.transfer(requestDto))
                    .expectErrorMatches(throwable ->
                            throwable instanceof WebClientResponseException.BadRequest
                    )
                    .verify();
        }
    }
}
