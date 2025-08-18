package com.cuentas.service.mapper;

import com.cuentas.dto.ProcessTransactionRequestDTO;
import com.cuentas.dto.ProcessTransactionResponseDTO;
import com.cuentas.dto.TransactionHistoryItemDTO;
import com.google.protobuf.Timestamp;
import com.transacciones.grpc.GetTransactionHistoryRequest;
import com.transacciones.grpc.ProcessTransactionRequest;
import com.transacciones.grpc.ProcessTransactionResponse;
import com.transacciones.grpc.TransactionHistoryItem;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Component
public class GrpcClientMapper {

    public ProcessTransactionRequest toGrpcRequest(ProcessTransactionRequestDTO internalDto) {
        return ProcessTransactionRequest.newBuilder()
                .setSourceAccountId(internalDto.getSourceAccountId())
                .setSourceBankId(internalDto.getSourceBankId())
                .setDestinationAccountId(internalDto.getDestinationAccountId())
                .setDestinationBankId(internalDto.getDestinationBankId())
                .setAmount(internalDto.getAmount().toPlainString())
                .setSourceAccountOriginalBalance(internalDto.getSourceAccountOriginalBalance().toPlainString())
                .setDescription(internalDto.getDescription())
                .build();
    }

    public ProcessTransactionResponseDTO fromGrpcResponse(ProcessTransactionResponse grpcResponse) {
        ProcessTransactionResponseDTO dto = new ProcessTransactionResponseDTO();
        dto.setTransferId(grpcResponse.getTransferId());
        dto.setStatus(grpcResponse.getStatus());
        dto.setTimestamp(fromGrpcTimestamp(grpcResponse.getTimestamp()));
        return dto;
    }

    public TransactionHistoryItemDTO fromGrpcHistoryItem(TransactionHistoryItem grpcItem) {
        TransactionHistoryItemDTO dto = new TransactionHistoryItemDTO();
        dto.setTransactionId(grpcItem.getTransactionId());
        dto.setTransferId(grpcItem.getTransferId());
        dto.setType(grpcItem.getType());
        dto.setAmount(new BigDecimal(grpcItem.getAmount()));
        dto.setStatus(grpcItem.getStatus());
        dto.setDate(fromGrpcTimestamp(grpcItem.getDate()));
        dto.setDescription(grpcItem.getDescription());
        return dto;
    }

    private LocalDateTime fromGrpcTimestamp(Timestamp timestamp) {
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos()), ZoneOffset.UTC);
    }
}
