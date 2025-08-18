package com.cuentas.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProcessTransactionRequestDTO {
    private Long sourceAccountId;
    private Long sourceBankId;
    private Long destinationAccountId;
    private Long destinationBankId;
    private BigDecimal amount;
    private BigDecimal sourceAccountOriginalBalance;
    private String description;
}
