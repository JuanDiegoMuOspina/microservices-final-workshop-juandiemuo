package com.cuentas.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransferRequestDTO {
    @NotNull
    private Long sourceAccountId;
    @NotNull
    private Long destinationAccountId;
    @NotNull
    @Positive
    private BigDecimal amount;
}
