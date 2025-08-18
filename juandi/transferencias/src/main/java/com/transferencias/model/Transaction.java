package com.transferencias.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table("transactions")
public class Transaction {

    @Id
    private Long id;
    private String transferId;
    private Long accountId;
    private String transactionType;
    private BigDecimal amount;
    private LocalDateTime transactionDate;
    private String status;
    private String description;

}
