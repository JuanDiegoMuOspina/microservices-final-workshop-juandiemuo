package com.transacciones.controller;

import com.transacciones.controller.mapper.GrpcMapper;
import com.transacciones.dto.ProcessTransactionRequestDTO;
import com.transacciones.dto.TransactionHistoryItemDTO;
import com.transacciones.dto.ProcessTransactionResponseDTO;
import com.transacciones.grpc.*;
import com.transacciones.service.TransactionService;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionGrpcControllerTest {

    @Mock
    private TransactionService transactionService;

    @Mock
    private GrpcMapper grpcMapper;

    @Mock
    private StreamObserver<ProcessTransactionResponse> processResponseObserver;

    @Mock
    private StreamObserver<TransactionHistoryItem> historyResponseObserver;

    @InjectMocks
    private TransactionGrpcController transactionGrpcController;

    @Test
    @DisplayName("processTransaction_whenServiceSucceeds_shouldSendResponseAndComplete")
    void processTransaction_whenServiceSucceeds_shouldSendResponseAndComplete() {
        // Arrange (Given)
        ProcessTransactionRequest request = ProcessTransactionRequest.newBuilder().build();

        ProcessTransactionRequestDTO requestDTO = ProcessTransactionRequestDTO.builder()
                .sourceAccountId(1L)
                .amount(new BigDecimal("100"))
                .build();

        ProcessTransactionResponseDTO responseDTO = ProcessTransactionResponseDTO.builder()
                .transferId("TXN-123")
                .status("COMPLETED")
                .timestamp(LocalDateTime.now())
                .build();

        ProcessTransactionResponse grpcResponse = ProcessTransactionResponse.newBuilder()
                .setTransferId(responseDTO.getTransferId())
                .setStatus(responseDTO.getStatus())
                .build();

        when(grpcMapper.toDto(request)).thenReturn(requestDTO);
        when(transactionService.processTransaction(requestDTO)).thenReturn(Mono.just(responseDTO));
        when(grpcMapper.toGrpcResponse(responseDTO)).thenReturn(grpcResponse);

        // Act (When)
        transactionGrpcController.processTransaction(request, processResponseObserver);

        // Assert (Then)
        verify(transactionService).processTransaction(requestDTO);
        verify(grpcMapper).toGrpcResponse(responseDTO);
        verify(processResponseObserver).onNext(grpcResponse);
        verify(processResponseObserver).onCompleted();
        verify(processResponseObserver, never()).onError(any());
    }

    @Test
    @DisplayName("processTransaction_whenServiceFails_shouldSendError")
    void processTransaction_whenServiceFails_shouldSendError() {
        // Arrange (Given)
        ProcessTransactionRequest request = ProcessTransactionRequest.newBuilder().build();
        ProcessTransactionRequestDTO requestDTO = new ProcessTransactionRequestDTO();
        RuntimeException error = new RuntimeException("Insufficient funds");

        when(grpcMapper.toDto(request)).thenReturn(requestDTO);
        when(transactionService.processTransaction(requestDTO)).thenReturn(Mono.error(error));

        // Act (When)
        transactionGrpcController.processTransaction(request, processResponseObserver);

        // Assert (Then)
        ArgumentCaptor<Throwable> errorCaptor = ArgumentCaptor.forClass(Throwable.class);
        verify(processResponseObserver).onError(errorCaptor.capture());
        assertEquals(error, errorCaptor.getValue());
        verify(processResponseObserver, never()).onNext(any());
        verify(processResponseObserver, never()).onCompleted();
    }


    @Test
    @DisplayName("getTransactionHistory_whenServiceFails_shouldSendError")
    void getTransactionHistory_whenServiceFails_shouldSendError() {
        // Arrange (Given)
        GetTransactionHistoryRequest request = GetTransactionHistoryRequest.newBuilder().setAccountId(555L).build();
        RuntimeException error = new RuntimeException("Database unavailable");

        when(transactionService.getTransactionHistory(555L)).thenReturn(Flux.error(error));

        // Act (When)
        transactionGrpcController.getTransactionHistory(request, historyResponseObserver);

        // Assert (Then)
        verify(historyResponseObserver).onError(error);
        verify(historyResponseObserver, never()).onNext(any());
        verify(historyResponseObserver, never()).onCompleted();
    }
}
