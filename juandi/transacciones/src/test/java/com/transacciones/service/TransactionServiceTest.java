package com.transacciones.service;

import com.transacciones.dto.ProcessTransactionRequestDTO;
import com.transacciones.dto.ProcessTransactionResponseDTO;
import com.transacciones.dto.TransactionHistoryItemDTO;
import com.transacciones.model.Transaction;
import com.transacciones.repository.TransactionRepository;
import com.transacciones.service.client.TranOrdersPublisher;
import com.transacciones.service.util.TransactionMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private TransactionMapper transactionMapper;

    @Mock
    private TranOrdersPublisher tranOrdersPublisher;

    @InjectMocks
    private TransactionService transactionService;

    @Captor
    private ArgumentCaptor<Transaction> transactionCaptor;

    @Captor
    private ArgumentCaptor<List<Transaction>> transactionListCaptor;

    @Test
    @DisplayName("processTransaction_whenIntraBankAndSufficientFunds_shouldSaveTwoTransactionsAndReturnCompleted")
    void processTransaction_whenIntraBankAndSufficientFunds_shouldSaveTwoTransactionsAndReturnCompleted() {
        // Arrange
        ProcessTransactionRequestDTO request = ProcessTransactionRequestDTO.builder()
                .sourceBankId(1L).destinationBankId(1L)
                .sourceAccountId(10L).destinationAccountId(20L)
                .amount(new BigDecimal("100")).amountOriginal(new BigDecimal("500"))
                .build();

        Transaction withdrawal = new Transaction();
        Transaction deposit = new Transaction();

        when(transactionMapper.createTransactionEntity(eq(request), anyString(), eq("WITHDRAWAL"), eq("COMPLETED"), eq(10L))).thenReturn(withdrawal);
        when(transactionMapper.createTransactionEntity(eq(request), anyString(), eq("DEPOSIT"), eq("COMPLETED"), eq(20L))).thenReturn(deposit);
        when(transactionRepository.saveAll(any(List.class))).thenReturn(Flux.just(withdrawal, deposit));

        // Act
        Mono<ProcessTransactionResponseDTO> result = transactionService.processTransaction(request);

        // Assert
        StepVerifier.create(result)
                .assertNext(response -> {
                    assertEquals("COMPLETED", response.getStatus());
                    assertNotNull(response.getTransferId());
                })
                .verifyComplete();

        verify(transactionRepository).saveAll(transactionListCaptor.capture());
        assertEquals(2, transactionListCaptor.getValue().size());
        verify(tranOrdersPublisher, never()).publishCartCreatedEvent(any());
    }

    @Test
    @DisplayName("processTransaction_whenIntraBankAndInsufficientFunds_shouldSaveOneFailedTransactionAndReturnFailed")
    void processTransaction_whenIntraBankAndInsufficientFunds_shouldSaveOneFailedTransactionAndReturnFailed() {
        // Arrange
        ProcessTransactionRequestDTO request = ProcessTransactionRequestDTO.builder()
                .sourceBankId(1L).destinationBankId(1L)
                .sourceAccountId(10L)
                .amount(new BigDecimal("500")).amountOriginal(new BigDecimal("100"))
                .build();

        Transaction failedWithdrawal = new Transaction();

        when(transactionMapper.createTransactionEntity(eq(request), anyString(), eq("WITHDRAWAL"), eq("FAILED"), eq(10L))).thenReturn(failedWithdrawal);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(Mono.just(failedWithdrawal));

        // Act
        Mono<ProcessTransactionResponseDTO> result = transactionService.processTransaction(request);

        // Assert
        StepVerifier.create(result)
                .assertNext(response -> {
                    assertEquals("FAILED", response.getStatus());
                    assertNotNull(response.getTransferId());
                })
                .verifyComplete();

        verify(transactionRepository).save(transactionCaptor.capture());
        assertEquals(failedWithdrawal, transactionCaptor.getValue());
    }

    @Test
    @DisplayName("processTransaction_whenInterBank_shouldSaveOnePendingTransactionAndPublishEvent")
    void processTransaction_whenInterBank_shouldSaveOnePendingTransactionAndPublishEvent() {
        // Arrange
        ProcessTransactionRequestDTO request = ProcessTransactionRequestDTO.builder()
                .sourceBankId(1L).destinationBankId(2L) // Different banks
                .sourceAccountId(10L).destinationAccountId(20L)
                .amount(new BigDecimal("100")).amountOriginal(new BigDecimal("500"))
                .build();

        Transaction pendingWithdrawal = new Transaction();
        Transaction depositMessage = new Transaction();

        when(transactionMapper.createTransactionEntity(eq(request), anyString(), eq("WITHDRAWAL"), eq("PENDING"), eq(10L))).thenReturn(pendingWithdrawal);
        when(transactionMapper.createTransactionEntity(eq(request), anyString(), eq("DEPOSIT"), eq("COMPLETED"), eq(20L))).thenReturn(depositMessage);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(Mono.just(pendingWithdrawal));
        doNothing().when(tranOrdersPublisher).publishCartCreatedEvent(any(Transaction.class));

        // Act
        Mono<ProcessTransactionResponseDTO> result = transactionService.processTransaction(request);

        // Assert
        StepVerifier.create(result)
                .assertNext(response -> {
                    assertEquals("PENDING", response.getStatus());
                    assertNotNull(response.getTransferId());
                })
                .verifyComplete();

        verify(transactionRepository).save(pendingWithdrawal);
        verify(tranOrdersPublisher).publishCartCreatedEvent(depositMessage);
    }

    @Test
    @DisplayName("getTransactionHistory_whenTransactionsExist_shouldReturnFluxOfHistoryItems")
    void getTransactionHistory_whenTransactionsExist_shouldReturnFluxOfHistoryItems() {
        // Arrange
        Long accountId = 123L;
        Transaction transaction1 = new Transaction();
        Transaction transaction2 = new Transaction();
        TransactionHistoryItemDTO dto1 = new TransactionHistoryItemDTO();
        TransactionHistoryItemDTO dto2 = new TransactionHistoryItemDTO();

        when(transactionRepository.findByAccountId(accountId)).thenReturn(Flux.just(transaction1, transaction2));
        when(transactionMapper.toHistoryDTO(transaction1)).thenReturn(dto1);
        when(transactionMapper.toHistoryDTO(transaction2)).thenReturn(dto2);

        // Act
        Flux<TransactionHistoryItemDTO> result = transactionService.getTransactionHistory(accountId);

        // Assert
        StepVerifier.create(result)
                .expectNext(dto1, dto2)
                .verifyComplete();
    }

    @Test
    @DisplayName("getTransactionHistory_whenNoTransactionsExist_shouldReturnEmptyFlux")
    void getTransactionHistory_whenNoTransactionsExist_shouldReturnEmptyFlux() {
        // Arrange
        Long accountId = 404L;
        when(transactionRepository.findByAccountId(accountId)).thenReturn(Flux.empty());

        // Act
        Flux<TransactionHistoryItemDTO> result = transactionService.getTransactionHistory(accountId);

        // Assert
        StepVerifier.create(result)
                .verifyComplete();
        verify(transactionMapper, never()).toHistoryDTO(any());
    }
}
