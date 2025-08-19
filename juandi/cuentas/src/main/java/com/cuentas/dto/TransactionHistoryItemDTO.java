package com.cuentas.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionHistoryItemDTO {
    private Long transactionId;
    private String transferId;
    private String type;
    private BigDecimal amount;
    private String status;
    private LocalDateTime date;
    private String description;
}
