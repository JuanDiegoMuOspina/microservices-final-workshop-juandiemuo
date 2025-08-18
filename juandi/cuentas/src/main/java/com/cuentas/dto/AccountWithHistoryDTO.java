package com.cuentas.dto;

import lombok.Data;

import java.util.List;

@Data
public class AccountWithHistoryDTO {
    private AccountResponseDTO accountInfo;
    private List<TransactionHistoryItemDTO> transactionHistory;
}
