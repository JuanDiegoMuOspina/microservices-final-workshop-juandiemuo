package com.cuentas.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AccountResponseDTO {

    private Long id;
    private String accountNumber;
    private String accountType;
    private BigDecimal balance;
    private String status;
    private Long bankId;
}
