package com.transacciones.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessTransactionRequestDTO {

    @NotNull
    @Positive
    private Long sourceAccountId;

    @NotNull
    @Positive
    private Long sourceBankId;

    @NotNull
    @Positive
    private Long destinationAccountId;

    @NotNull
    @Positive
    private Long destinationBankId;

    @NotNull
    @Positive
    private BigDecimal amount;

    @NotNull // El saldo original es requerido para la validación de fondos
    private BigDecimal amountOriginal;

    private String description;
}
