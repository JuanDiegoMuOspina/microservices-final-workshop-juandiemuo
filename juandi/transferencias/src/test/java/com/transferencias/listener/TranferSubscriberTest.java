package com.transferencias.listener;

import com.transferencias.dto.Transaction;
import com.transferencias.service.TransferService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TranferSubscriberTest {

    @Mock
    private TransferService transferService;

    @InjectMocks
    private TranferSubscriber tranferSubscriber;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("receiveCart_whenTransactionReceived_shouldCallSaveTransferencia")
    void receiveCart_whenTransactionReceived_shouldCallSaveTransferencia() {
        // Arrange (Given)
        Transaction transaction = Transaction.builder()
                .id(1L)
                .transferId("some-transfer-id")
                .accountId(123L)
                .transactionType("DEBIT")
                .amount(new BigDecimal("100.00"))
                .transactionDate(LocalDateTime.now())
                .status("PENDING")
                .description("Test transaction")
                .build();

        when(transferService.saveTransferencia(any(Transaction.class))).thenReturn(Mono.empty());

        // Act (When)
        tranferSubscriber.receiveCart(transaction);

        // Assert (Then)
        verify(transferService).saveTransferencia(transaction);
    }
}
