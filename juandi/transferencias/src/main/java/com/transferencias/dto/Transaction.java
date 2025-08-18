package com.transferencias.dto;

import lombok.Data;


import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Transaction {


    private Long id;
    private String transferId;
    private Long accountId;
    private String transactionType;
    private BigDecimal amount;
    private LocalDateTime transactionDate;
    private String status;
    private String description;


}
