package com.transacciones.controller.mapper;

import com.google.protobuf.Timestamp;
import com.transacciones.dto.ProcessTransactionRequestDTO;
import com.transacciones.dto.ProcessTransactionResponseDTO;
import com.transacciones.dto.TransactionHistoryItemDTO;
import com.transacciones.grpc.ProcessTransactionRequest;
import com.transacciones.grpc.ProcessTransactionResponse;
import com.transacciones.grpc.TransactionHistoryItem;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Component
public class GrpcMapper {

    public ProcessTransactionRequestDTO toDto(ProcessTransactionRequest grpcRequest) {
        ProcessTransactionRequestDTO dto = new ProcessTransactionRequestDTO();
        dto.setSourceAccountId(grpcRequest.getSourceAccountId());
        dto.setSourceBankId(grpcRequest.getSourceBankId());
        dto.setDestinationAccountId(grpcRequest.getDestinationAccountId());
        dto.setDestinationBankId(grpcRequest.getDestinationBankId());
        dto.setAmount(new BigDecimal(grpcRequest.getAmount()));
        dto.setAmountOriginal(new BigDecimal(grpcRequest.getSourceAccountOriginalBalance()));
        dto.setDescription(grpcRequest.getDescription());
        return dto;
    }

    public ProcessTransactionResponse toGrpcResponse(ProcessTransactionResponseDTO dto) {
        Instant instant = dto.getTimestamp().toInstant(ZoneOffset.UTC);
        Timestamp timestamp = Timestamp.newBuilder()
                .setSeconds(instant.getEpochSecond())
                .setNanos(instant.getNano())
                .build();

        return ProcessTransactionResponse.newBuilder()
                .setTransferId(dto.getTransferId())
                .setStatus(dto.getStatus())
                .setTimestamp(timestamp)
                .build();
    }

    public TransactionHistoryItem toGrpcHistoryItem(TransactionHistoryItemDTO dto) {
        Instant instant = dto.getDate().toInstant(ZoneOffset.UTC);
        Timestamp timestamp = Timestamp.newBuilder()
                .setSeconds(instant.getEpochSecond())
                .setNanos(instant.getNano())
                .build();

        return TransactionHistoryItem.newBuilder()
                .setTransactionId(dto.getTransactionId())
                .setTransferId(dto.getTransferId())
                .setType(dto.getType())
                .setAmount(dto.getAmount().toPlainString())
                .setStatus(dto.getStatus())
                .setDate(timestamp)
                .setDescription(dto.getDescription())
                .build();
    }
}
