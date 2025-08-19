package com.transacciones.service.util;

import com.transacciones.dto.ProcessTransactionRequestDTO;
import com.transacciones.dto.TransactionHistoryItemDTO;
import com.transacciones.model.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class TransactionMapperTest {

    private TransactionMapper transactionMapper;

    @BeforeEach
    void setUp() {
        transactionMapper = new TransactionMapper();
    }

    @Test
    @DisplayName("toHistoryDTO_shouldMapAllFieldsCorrectly")
    void toHistoryDTO_shouldMapAllFieldsCorrectly() {
        // Arrange (Given)
        Transaction transaction = new Transaction();
        transaction.setId(1L);
        transaction.setTransferId("TRANSFER-001");
        transaction.setTransactionType("DEPOSIT");
        transaction.setAmount(new BigDecimal("150.50"));
        transaction.setStatus("COMPLETED");
        transaction.setTransactionDate(LocalDateTime.of(2024, 5, 20, 10, 30));
        transaction.setDescription("Test deposit");

        // Act (When)
        TransactionHistoryItemDTO dto = transactionMapper.toHistoryDTO(transaction);

        // Assert (Then)
        assertNotNull(dto);
        assertEquals(transaction.getId(), dto.getTransactionId());
        assertEquals(transaction.getTransferId(), dto.getTransferId());
        assertEquals(transaction.getTransactionType(), dto.getType());
        assertEquals(transaction.getAmount(), dto.getAmount());
        assertEquals(transaction.getStatus(), dto.getStatus());
        assertEquals(transaction.getTransactionDate(), dto.getDate());
        assertEquals(transaction.getDescription(), dto.getDescription());
    }

    @Test
    @DisplayName("createTransactionEntity_shouldMapAllFieldsCorrectly")
    void createTransactionEntity_shouldMapAllFieldsCorrectly() {
        // Arrange (Given)
        ProcessTransactionRequestDTO dto = ProcessTransactionRequestDTO.builder()
                .amount(new BigDecimal("200.00"))
                .description("Test withdrawal")
                .build();
        String transferId = "TRANSFER-002";
        String type = "WITHDRAWAL";
        String status = "PENDING";
        Long accountId = 12345L;

        // Act (When)
        Transaction transaction = transactionMapper.createTransactionEntity(dto, transferId, type, status, accountId);

        // Assert (Then)
        assertNotNull(transaction);
        assertEquals(transferId, transaction.getTransferId());
        assertEquals(accountId, transaction.getAccountId());
        assertEquals(type, transaction.getTransactionType());
        assertEquals(dto.getAmount(), transaction.getAmount());
        assertEquals(status, transaction.getStatus());
        assertEquals(dto.getDescription(), transaction.getDescription());
        assertNotNull(transaction.getTransactionDate()); // Verify that the date is set
    }
}
