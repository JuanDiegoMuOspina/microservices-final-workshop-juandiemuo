package com.transferencias.service;

import com.transferencias.dto.Transaction;
import com.transferencias.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TransferServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransferService transferService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("saveTransferencia_whenRepositorySavesSuccessfully_shouldComplete")
    void saveTransferencia_whenRepositorySavesSuccessfully_shouldComplete() {
        // Arrange (Given)
        Transaction transactionDto = Transaction.builder()
                .transferId("TXN-001")
                .accountId(1L)
                .transactionType("CREDIT")
                .amount(new BigDecimal("250.75"))
                .transactionDate(LocalDateTime.now())
                .status("COMPLETED")
                .description("Successful transaction")
                .build();

        com.transferencias.model.Transaction savedTransaction = new com.transferencias.model.Transaction();
        savedTransaction.setId(1L);

        ArgumentCaptor<com.transferencias.model.Transaction> transactionCaptor = ArgumentCaptor.forClass(com.transferencias.model.Transaction.class);

        when(transactionRepository.save(transactionCaptor.capture())).thenReturn(Mono.just(savedTransaction));

        // Act (When)
        Mono<Void> result = transferService.saveTransferencia(transactionDto);

        // Assert (Then)
        StepVerifier.create(result)
                .verifyComplete();

        verify(transactionRepository).save(any(com.transferencias.model.Transaction.class));
        assertEquals(transactionDto.getTransferId(), transactionCaptor.getValue().getTransferId());
        assertEquals(transactionDto.getAmount(), transactionCaptor.getValue().getAmount());
    }

    @Test
    @DisplayName("saveTransferencia_whenRepositoryFails_shouldReturnError")
    void saveTransferencia_whenRepositoryFails_shouldReturnError() {
        // Arrange (Given)
        Transaction transactionDto = Transaction.builder()
                .transferId("TXN-002")
                .build();

        RuntimeException dbError = new RuntimeException("Database connection failed");
        when(transactionRepository.save(any(com.transferencias.model.Transaction.class))).thenReturn(Mono.error(dbError));

        // Act (When)
        Mono<Void> result = transferService.saveTransferencia(transactionDto);

        // Assert (Then)
        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();
    }
}
