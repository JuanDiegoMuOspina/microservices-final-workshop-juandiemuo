package com.cuentas.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TransactionHistoryItemDTO {
    private Long transactionId;
    private String transferId;
    private String type;
    private BigDecimal amount;
    private String status;
    private LocalDateTime date;
    private String description;
}
