package com.zarnab.panel.clients.service.faraboom;

import com.zarnab.panel.clients.dto.faraboom.FaraboomStatementResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
public class FaraboomStatementClient {

    private final WebClient faraboomWebClient;
    private final FaraboomTokenService tokenService;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd");

    public FaraboomStatementClient(@Qualifier("faraboomWebClient") WebClient faraboomWebClient, FaraboomTokenService tokenService) {
        this.faraboomWebClient = faraboomWebClient;
        this.tokenService = tokenService;
    }


    /**
     * Retrieves an account statement for a given deposit number within a date range.
     * This is a GET request and is idempotent, so our global filter will automatically retry it on transient failures.
     *
     * @param depositNumber The account/deposit number to query.
     * @param fromDate      The start date for the statement.
     * @param toDate        The end date for the statement.
     * @param offset        The starting point of the transaction list (for pagination).
     * @param length        The number of transactions to retrieve (for pagination).
     * @return A Mono emitting the statement response.
     */
    public Mono<FaraboomStatementResponse> getStatement(
            String depositNumber,
            LocalDate fromDate,
            LocalDate toDate,
            int offset,
            int length) {

        return tokenService.getAccessToken()
                .flatMap(token -> faraboomWebClient.get()
                        .uri(uriBuilder -> uriBuilder
                                .path("/v1/deposits/{depositNumber}/statements")
                                .queryParam("from_date", fromDate != null ? fromDate.format(DATE_FORMATTER) : null)
                                .queryParam("to_date", toDate != null ? toDate.format(DATE_FORMATTER) : null)
                                .queryParam("page", offset)
                                .queryParam("size", length)
                                .build(depositNumber)
                        )
                        // Add the dynamic Authorization header required for this specific call
                        .header("Authorization", "Bearer " + token)
                        .retrieve()
                        .bodyToMono(FaraboomStatementResponse.class)
                );
    }
}