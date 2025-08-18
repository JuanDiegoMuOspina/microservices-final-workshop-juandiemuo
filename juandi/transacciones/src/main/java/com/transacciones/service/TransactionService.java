package com.transacciones.service;

import com.transacciones.dto.ProcessTransactionRequestDTO;
import com.transacciones.dto.ProcessTransactionResponseDTO;
import com.transacciones.dto.TransactionHistoryItemDTO;
import com.transacciones.model.Transaction;
import com.transacciones.repository.TransactionRepository;
import com.transacciones.service.util.TransactionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;

    public Mono<ProcessTransactionResponseDTO> processTransaction(ProcessTransactionRequestDTO requestDTO) {
        String transferId = UUID.randomUUID().toString();

        if (requestDTO.getSourceBankId().equals(requestDTO.getDestinationBankId())) {
            // Transferencia dentro del mismo banco
            boolean hasSufficientFunds = requestDTO.getAmountOriginal().compareTo(requestDTO.getAmount()) >= 0;

            if (!hasSufficientFunds) {
                // Fondos insuficientes: crear una transacción de retiro fallida
                Transaction failedWithdrawal = transactionMapper.createTransactionEntity(requestDTO, transferId, "WITHDRAWAL", "FAILED", requestDTO.getSourceAccountId());
                return transactionRepository.save(failedWithdrawal)
                        .thenReturn(createResponse(transferId, "FAILED"));
            }

            // Fondos suficientes: crear dos transacciones completadas
            Transaction withdrawal = transactionMapper.createTransactionEntity(requestDTO, transferId, "WITHDRAWAL", "COMPLETED", requestDTO.getSourceAccountId());
            Transaction deposit = transactionMapper.createTransactionEntity(requestDTO, transferId, "DEPOSIT", "COMPLETED", requestDTO.getDestinationAccountId());

            return transactionRepository.saveAll(Arrays.asList(withdrawal, deposit))
                    .then(Mono.just(createResponse(transferId, "COMPLETED")));

        } else {
            // Transferencia interbancaria: modelo pendiente con colas
            System.out.println("Pendiente implementar modelo de colas con RabbitMQ para transferencia interbancaria.");
            Transaction pendingWithdrawal = transactionMapper.createTransactionEntity(requestDTO, transferId, "WITHDRAWAL", "PENDING", requestDTO.getSourceAccountId());
            return transactionRepository.save(pendingWithdrawal)
                    .thenReturn(createResponse(transferId, "PENDING"));
        }
    }

    public Flux<TransactionHistoryItemDTO> getTransactionHistory(Long accountId) {
        return transactionRepository.findByAccountId(accountId)
                .map(transactionMapper::toHistoryDTO);
    }

    private ProcessTransactionResponseDTO createResponse(String transferId, String status) {
        ProcessTransactionResponseDTO response = new ProcessTransactionResponseDTO();
        response.setTransferId(transferId);
        response.setStatus(status);
        response.setTimestamp(LocalDateTime.now());
        return response;
    }
}
