package com.cuentas.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransferRequestDTO {
    @NotNull
    private Long sourceAccountId;
    @NotNull
    private Long destinationAccountId;
    @NotNull
    @Positive
    private BigDecimal amount;
}
