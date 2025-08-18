package com.transacciones.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
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
