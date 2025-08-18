package com.transacciones.service.util;

import com.transacciones.dto.ProcessTransactionRequestDTO;
import com.transacciones.dto.TransactionHistoryItemDTO;
import com.transacciones.model.Transaction;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class TransactionMapper {

    public TransactionHistoryItemDTO toHistoryDTO(Transaction transaction) {
        TransactionHistoryItemDTO dto = new TransactionHistoryItemDTO();
        dto.setTransactionId(transaction.getId());
        dto.setTransferId(transaction.getTransferId());
        dto.setType(transaction.getTransactionType());
        dto.setAmount(transaction.getAmount());
        dto.setStatus(transaction.getStatus());
        dto.setDate(transaction.getTransactionDate());
        dto.setDescription(transaction.getDescription());
        return dto;
    }

    public Transaction createTransactionEntity(ProcessTransactionRequestDTO dto, String transferId, String type, String status, Long accountId) {
        Transaction transaction = new Transaction();
        transaction.setTransferId(transferId);
        transaction.setAccountId(accountId);
        transaction.setTransactionType(type);
        transaction.setAmount(dto.getAmount());
        transaction.setTransactionDate(LocalDateTime.now());
        transaction.setStatus(status);
        transaction.setDescription(dto.getDescription());
        return transaction;
    }
}
