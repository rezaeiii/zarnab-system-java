package com.zarnab.panel.clients.dto.faraboom;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

// Using a Java Record for a concise, immutable request object
public record FaraboomTransferRequest(

    @JsonProperty("source_deposit")
    String sourceDeposit,

    @JsonProperty("destination_deposit")
    String destinationDeposit,

    @JsonProperty("amount")
    String amount
//
//    @JsonProperty("customer_number")
//    String customerNumber,
//
//    @JsonProperty("source_comment")
//    String sourceComment,
//
//    @JsonProperty("destination_comment")
//    String destinationComment,
//
//    @JsonProperty("pay_id")
//    String payId,
//
//    // This reference number is set by the client
//    @JsonProperty("reference_number")
//    String clientReferenceNumber,
//
//    @JsonProperty("additional_document_desc")
//    String additionalDocumentDesc,
//
//    // Example: "POSA". This could also be an Enum.
//    @JsonProperty("transaction_reason")
//    String transactionReason
) {}