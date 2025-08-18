package com.cuentas.service;

import com.cuentas.dto.*;
import com.cuentas.model.Account;
import com.cuentas.repository.AccountRepository;
import com.cuentas.service.client.BankServiceClient;
import com.cuentas.service.client.TransactionServiceClient;
import com.cuentas.service.exception.AccountAlreadyExistsException;
import com.cuentas.service.exception.InsufficientFundsException;
import com.cuentas.service.util.AccountMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final BankServiceClient bankServiceClient;
    private final TransactionServiceClient transactionServiceClient;
    private final AccountMapper accountMapper;

    public Mono<AccountResponseDTO> create(CreateAccountRequestDTO requestDTO) {
        Mono<Void> checkAccountExists = accountRepository.findByAccountNumber(requestDTO.getAccountNumber())
                .flatMap(existingAccount -> Mono.error(new AccountAlreadyExistsException("La cuenta con el número " + requestDTO.getAccountNumber() + " ya existe.")))
                .then();

        return bankServiceClient.validateBankExists(requestDTO.getBankId())
                .then(checkAccountExists)
                .then(Mono.defer(() -> {
                    Account newAccount = accountMapper.toEntity(requestDTO);
                    newAccount.setBalance(BigDecimal.ZERO);
                    newAccount.setStatus("ACTIVE");
                    return accountRepository.save(newAccount);
                }))
                .map(accountMapper::toResponseDTO);
    }

    public Mono<AccountResponseDTO> findById(Long id) {
        return accountRepository.findById(id)
                .map(accountMapper::toResponseDTO);
    }

    public Mono<AccountResponseDTO> findByAccountNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
                .map(accountMapper::toResponseDTO);
    }

    public Flux<AccountResponseDTO> findByBankId(Long bankId) {
        return accountRepository.findByBankId(bankId)
                .map(accountMapper::toResponseDTO);
    }

    public Mono<AccountResponseDTO> updateStatus(Long id, UpdateAccountStatusRequestDTO requestDTO) {
        return accountRepository.findById(id)
                .flatMap(account -> {
                    account.setStatus(requestDTO.getStatus());
                    return accountRepository.save(account);
                })
                .map(accountMapper::toResponseDTO);
    }

    public Mono<ProcessTransactionResponseDTO> createTransfer(TransferRequestDTO transferRequest) {
        Mono<Account> sourceAccountMono = accountRepository.findById(transferRequest.getSourceAccountId());
        Mono<Account> destinationAccountMono = accountRepository.findById(transferRequest.getDestinationAccountId());

        return Mono.zip(sourceAccountMono, destinationAccountMono)
                .flatMap(tuple -> {
                    Account sourceAccount = tuple.getT1();
                    Account destinationAccount = tuple.getT2();

                    if (sourceAccount.getBalance().compareTo(transferRequest.getAmount()) < 0) {
                        return Mono.error(new InsufficientFundsException("Fondos insuficientes en la cuenta de origen."));
                    }

                    ProcessTransactionRequestDTO processRequestDto = new ProcessTransactionRequestDTO();
                    processRequestDto.setSourceAccountId(sourceAccount.getId());
                    processRequestDto.setSourceBankId(sourceAccount.getBankId());
                    processRequestDto.setDestinationAccountId(destinationAccount.getId());
                    processRequestDto.setDestinationBankId(destinationAccount.getBankId());
                    processRequestDto.setAmount(transferRequest.getAmount());
                    processRequestDto.setSourceAccountOriginalBalance(sourceAccount.getBalance());
                    processRequestDto.setDescription("Transferencia entre cuentas");

                    return Mono.fromCallable(() -> transactionServiceClient.processTransaction(processRequestDto))
                            .subscribeOn(Schedulers.boundedElastic())
                            .flatMap(response -> {
                                if ("COMPLETED".equals(response.getStatus())) {
                                    sourceAccount.setBalance(sourceAccount.getBalance().subtract(transferRequest.getAmount()));
                                    destinationAccount.setBalance(destinationAccount.getBalance().add(transferRequest.getAmount()));
                                    return accountRepository.saveAll(List.of(sourceAccount, destinationAccount))
                                            .then(Mono.just(response));
                                }
                                return Mono.just(response);
                            });
                });
    }

    public Mono<AccountWithHistoryDTO> findByIdWithHistory(Long accountId) {
        Mono<Account> accountMono = accountRepository.findById(accountId);
        Mono<List<TransactionHistoryItemDTO>> historyMono = Mono.fromCallable(() -> transactionServiceClient.getTransactionHistory(accountId))
                .subscribeOn(Schedulers.boundedElastic());

        return Mono.zip(accountMono, historyMono)
                .map(tuple -> {
                    Account account = tuple.getT1();
                    List<TransactionHistoryItemDTO> history = tuple.getT2();

                    AccountWithHistoryDTO result = new AccountWithHistoryDTO();
                    result.setAccountInfo(accountMapper.toResponseDTO(account));
                    result.setTransactionHistory(history);
                    return result;
                });
    }
}
