package com.transacciones.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessTransactionResponseDTO {

    private String transferId;
    private String status;
    private LocalDateTime timestamp;
}
