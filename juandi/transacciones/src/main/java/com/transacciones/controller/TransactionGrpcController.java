package com.transacciones.controller;

import com.transacciones.controller.mapper.GrpcMapper;
import com.transacciones.grpc.*;
import com.transacciones.service.TransactionService;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import org.springframework.grpc.server.service.GrpcService;


@GrpcService
@RequiredArgsConstructor
public class TransactionGrpcController extends TransactionServiceGrpc.TransactionServiceImplBase {

    private final TransactionService transactionService;
    private final GrpcMapper grpcMapper;

    @Override
    public void processTransaction(ProcessTransactionRequest request, StreamObserver<ProcessTransactionResponse> responseObserver) {
        transactionService.processTransaction(grpcMapper.toDto(request))
                .subscribe(
                        responseDTO -> {
                            responseObserver.onNext(grpcMapper.toGrpcResponse(responseDTO));
                            responseObserver.onCompleted();
                        },
                        responseObserver::onError
                );
    }

    @Override
    public void getTransactionHistory(GetTransactionHistoryRequest request, StreamObserver<TransactionHistoryItem> responseObserver) {
        transactionService.getTransactionHistory(request.getAccountId())
                .subscribe(
                        historyItemDTO -> responseObserver.onNext(grpcMapper.toGrpcHistoryItem(historyItemDTO)),
                        responseObserver::onError,
                        responseObserver::onCompleted
                );
    }
}
