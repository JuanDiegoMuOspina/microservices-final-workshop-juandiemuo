package com.cuentas.service.client;

import com.cuentas.dto.ProcessTransactionRequestDTO;
import com.cuentas.dto.ProcessTransactionResponseDTO;
import com.cuentas.dto.TransactionHistoryItemDTO;
import com.cuentas.service.mapper.GrpcClientMapper;
import com.transacciones.grpc.GetTransactionHistoryRequest;
import com.transacciones.grpc.ProcessTransactionRequest;
import com.transacciones.grpc.ProcessTransactionResponse;
import com.transacciones.grpc.TransactionHistoryItem;
import com.transacciones.grpc.TransactionServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
//@RequiredArgsConstructor
public class TransactionServiceClient {

    private ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 9090).usePlaintext().build();
    private TransactionServiceGrpc.TransactionServiceBlockingStub blockingStub= TransactionServiceGrpc.newBlockingStub(channel) ;

    private final GrpcClientMapper grpcClientMapper = new GrpcClientMapper();

    public ProcessTransactionResponseDTO processTransaction(ProcessTransactionRequestDTO requestDTO) {
        ProcessTransactionRequest grpcRequest = grpcClientMapper.toGrpcRequest(requestDTO);
        ProcessTransactionResponse grpcResponse = blockingStub.processTransaction(grpcRequest);
        return grpcClientMapper.fromGrpcResponse(grpcResponse);
    }

    public List<TransactionHistoryItemDTO> getTransactionHistory(Long accountId) {
        GetTransactionHistoryRequest request = GetTransactionHistoryRequest.newBuilder()
                .setAccountId(accountId)
                .build();

        Iterator<TransactionHistoryItem> grpcResponseIterator = blockingStub.getTransactionHistory(request);

        List<TransactionHistoryItemDTO> transactionHistory = new ArrayList<>();
        grpcResponseIterator.forEachRemaining(grpcItem ->
                transactionHistory.add(grpcClientMapper.fromGrpcHistoryItem(grpcItem))
        );

        return transactionHistory;
    }
}
