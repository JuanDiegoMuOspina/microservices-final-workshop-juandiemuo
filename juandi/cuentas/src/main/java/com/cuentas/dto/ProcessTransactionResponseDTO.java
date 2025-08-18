package com.cuentas.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProcessTransactionResponseDTO {
    private String transferId;
    private String status;
    private LocalDateTime timestamp;
}
