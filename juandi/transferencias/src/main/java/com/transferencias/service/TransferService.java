package com.transferencias.service;

import com.transferencias.dto.Transaction;
import com.transferencias.repository.TransactionRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
@Slf4j
public class TransferService {

    private final TransactionRepository transactionRepository;

    public Mono<Void> saveTransferencia(Transaction transactionDto) {
        return Mono.fromCallable(() -> {
            com.transferencias.model.Transaction transaction = com.transferencias.model.Transaction.builder()
                    .transferId(transactionDto.getTransferId())
                    .accountId(transactionDto.getAccountId())
                    .transactionType(transactionDto.getTransactionType())
                    .amount(transactionDto.getAmount())
                    .transactionDate(transactionDto.getTransactionDate())
                    .status(transactionDto.getStatus())
                    .description(transactionDto.getDescription())
                    .build();
            return transaction;
        })
        .flatMap(transactionRepository::save)
        .doOnSuccess(savedTransaction -> log.info("Transferencia guardada exitosamente con ID: {}", savedTransaction.getId()))
        .then();
    }
}
