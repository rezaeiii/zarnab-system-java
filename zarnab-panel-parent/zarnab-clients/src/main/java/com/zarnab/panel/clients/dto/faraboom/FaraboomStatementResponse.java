package com.zarnab.panel.clients.dto.faraboom;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FaraboomStatementResponse {

    @JsonProperty("statements")
    private List<Statement> statements;

    @JsonProperty("has_next_page")
    private boolean hasNextPage;

    @JsonProperty("next_offset")
    private Long nextOffset;

    @JsonProperty("operation_time")
    private String operationTime;

    @JsonProperty("ref_id")
    private String refId;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Statement {
        @JsonProperty("serial_number")
        private String serialNumber;

        @JsonProperty("transaction_date")
        private String transactionDate; // Using String for robustness with date-time formats

        @JsonProperty("description")
        private String description;

        @JsonProperty("creditor")
        private String creditor;

        @JsonProperty("balance")
        private BigDecimal balance;

        @JsonProperty("statement_serial")
        private Long statementSerial;

        @JsonProperty("branch_code")
        private String branchCode;

        @JsonProperty("branch_name")
        private String branchName;
        
        @JsonProperty("transfer_amount")
        private BigDecimal transferAmount;

        @JsonProperty("payment_id")
        private String paymentId;

        @JsonProperty("reference_number")
        private String referenceNumber;

        @JsonProperty("terminal_id")
        private String terminalId;

        @JsonProperty("statement_id")
        private String statementId;
    }
}