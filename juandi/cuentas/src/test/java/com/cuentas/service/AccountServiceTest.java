package com.cuentas.service;

import com.cuentas.dto.*;
import com.cuentas.model.Account;
import com.cuentas.repository.AccountRepository;
import com.cuentas.service.client.BankServiceClient;
import com.cuentas.service.client.TransactionServiceClient;
import com.cuentas.service.exception.AccountAlreadyExistsException;
import com.cuentas.service.exception.InsufficientFundsException;
import com.cuentas.service.util.AccountMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private BankServiceClient bankServiceClient;

    @Mock
    private TransactionServiceClient transactionServiceClient;

    @Mock
    private AccountMapper accountMapper;

    @InjectMocks
    private AccountService accountService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void create_whenAccountDoesNotExist_shouldCreateAccount() {
        CreateAccountRequestDTO request = CreateAccountRequestDTO.builder()
                .accountNumber("12345")
                .accountType("SAVINGS")
                .bankId(1L)
                .build();

        Account account = new Account("12345", "SAVINGS", BigDecimal.ZERO, "ACTIVE", 1L);
        account.setId(1L);

        AccountResponseDTO responseDTO = AccountResponseDTO.builder()
                .id(1L)
                .accountNumber("12345")
                .accountType("SAVINGS")
                .balance(BigDecimal.ZERO)
                .status("ACTIVE")
                .bankId(1L)
                .build();

        when(accountRepository.findByAccountNumber(request.getAccountNumber())).thenReturn(Mono.empty());
        when(bankServiceClient.validateBankExists(request.getBankId())).thenReturn(Mono.empty());
        when(accountMapper.toEntity(request)).thenReturn(new Account(request.getAccountNumber(), request.getAccountType(), null, null, request.getBankId()));
        when(accountRepository.save(any(Account.class))).thenReturn(Mono.just(account));
        when(accountMapper.toResponseDTO(account)).thenReturn(responseDTO);

        StepVerifier.create(accountService.create(request))
                .expectNext(responseDTO)
                .verifyComplete();
    }



    @Test
    void findById_whenAccountExists_shouldReturnAccount() {
        Account account = new Account("12345", "SAVINGS", BigDecimal.TEN, "ACTIVE", 1L);
        account.setId(1L);
        AccountResponseDTO responseDTO = AccountResponseDTO.builder()
                .id(1L)
                .accountNumber("12345")
                .accountType("SAVINGS")
                .balance(BigDecimal.TEN)
                .status("ACTIVE")
                .bankId(1L)
                .build();

        when(accountRepository.findById(1L)).thenReturn(Mono.just(account));
        when(accountMapper.toResponseDTO(account)).thenReturn(responseDTO);

        StepVerifier.create(accountService.findById(1L))
                .expectNext(responseDTO)
                .verifyComplete();
    }

    @Test
    void findByAccountNumber_whenAccountExists_shouldReturnAccount() {
        Account account = new Account("12345", "SAVINGS", BigDecimal.TEN, "ACTIVE", 1L);
        account.setId(1L);
        AccountResponseDTO responseDTO = AccountResponseDTO.builder()
                .id(1L)
                .accountNumber("12345")
                .accountType("SAVINGS")
                .balance(BigDecimal.TEN)
                .status("ACTIVE")
                .bankId(1L)
                .build();

        when(accountRepository.findByAccountNumber("12345")).thenReturn(Mono.just(account));
        when(accountMapper.toResponseDTO(account)).thenReturn(responseDTO);

        StepVerifier.create(accountService.findByAccountNumber("12345"))
                .expectNext(responseDTO)
                .verifyComplete();
    }

    @Test
    void findByBankId_shouldReturnAccounts() {
        Account account = new Account("12345", "SAVINGS", BigDecimal.TEN, "ACTIVE", 1L);
        account.setId(1L);
        AccountResponseDTO responseDTO = AccountResponseDTO.builder()
                .id(1L)
                .accountNumber("12345")
                .accountType("SAVINGS")
                .balance(BigDecimal.TEN)
                .status("ACTIVE")
                .bankId(1L)
                .build();

        when(accountRepository.findByBankId(1L)).thenReturn(Flux.just(account));
        when(accountMapper.toResponseDTO(account)).thenReturn(responseDTO);

        StepVerifier.create(accountService.findByBankId(1L))
                .expectNext(responseDTO)
                .verifyComplete();
    }

    @Test
    void updateStatus_whenAccountExists_shouldUpdateStatus() {
        UpdateAccountStatusRequestDTO request = UpdateAccountStatusRequestDTO.builder().status("INACTIVE").build();
        Account account = new Account("12345", "SAVINGS", BigDecimal.TEN, "ACTIVE", 1L);
        account.setId(1L);
        Account updatedAccount = new Account("12345", "SAVINGS", BigDecimal.TEN, "INACTIVE", 1L);
        updatedAccount.setId(1L);
        AccountResponseDTO responseDTO = AccountResponseDTO.builder()
                .id(1L)
                .accountNumber("12345")
                .accountType("SAVINGS")
                .balance(BigDecimal.TEN)
                .status("INACTIVE")
                .bankId(1L)
                .build();

        when(accountRepository.findById(1L)).thenReturn(Mono.just(account));
        when(accountRepository.save(any(Account.class))).thenReturn(Mono.just(updatedAccount));
        when(accountMapper.toResponseDTO(updatedAccount)).thenReturn(responseDTO);

        StepVerifier.create(accountService.updateStatus(1L, request))
                .expectNext(responseDTO)
                .verifyComplete();
    }

    @Test
    void createTransfer_whenFundsAreSufficient_shouldProcessTransfer() {
        TransferRequestDTO request = TransferRequestDTO.builder()
                .sourceAccountId(1L)
                .destinationAccountId(2L)
                .amount(new BigDecimal("100"))
                .build();

        Account sourceAccount = new Account("111", "SAVINGS", new BigDecimal("200"), "ACTIVE", 1L);
        sourceAccount.setId(1L);
        Account destAccount = new Account("222", "SAVINGS", new BigDecimal("50"), "ACTIVE", 2L);
        destAccount.setId(2L);

        ProcessTransactionResponseDTO transactionResponse = new ProcessTransactionResponseDTO("123", "COMPLETED", null);

        when(accountRepository.findById(1L)).thenReturn(Mono.just(sourceAccount));
        when(accountRepository.findById(2L)).thenReturn(Mono.just(destAccount));
        when(transactionServiceClient.processTransaction(any(ProcessTransactionRequestDTO.class))).thenReturn(transactionResponse);
        when(accountRepository.saveAll(any(List.class))).thenReturn(Flux.empty());

        StepVerifier.create(accountService.createTransfer(request))
                .expectNext(transactionResponse)
                .verifyComplete();
    }

    @Test
    void createTransfer_whenFundsAreInsufficient_shouldReturnError() {
        TransferRequestDTO request = TransferRequestDTO.builder()
                .sourceAccountId(1L)
                .destinationAccountId(2L)
                .amount(new BigDecimal("300"))
                .build();

        Account sourceAccount = new Account("111", "SAVINGS", new BigDecimal("200"), "ACTIVE", 1L);
        sourceAccount.setId(1L);
        Account destAccount = new Account("222", "SAVINGS", new BigDecimal("50"), "ACTIVE", 2L);
        destAccount.setId(2L);

        when(accountRepository.findById(1L)).thenReturn(Mono.just(sourceAccount));
        when(accountRepository.findById(2L)).thenReturn(Mono.just(destAccount));

        StepVerifier.create(accountService.createTransfer(request))
                .expectError(InsufficientFundsException.class)
                .verify();
    }

    @Test
    void findByIdWithHistory_shouldReturnAccountAndHistory() {
        Account account = new Account("12345", "SAVINGS", BigDecimal.TEN, "ACTIVE", 1L);
        account.setId(1L);
        AccountResponseDTO accountResponseDTO = AccountResponseDTO.builder()
                .id(1L)
                .accountNumber("12345")
                .accountType("SAVINGS")
                .balance(BigDecimal.TEN)
                .status("ACTIVE")
                .bankId(1L)
                .build();

        TransactionHistoryItemDTO historyItem = new TransactionHistoryItemDTO();
        List<TransactionHistoryItemDTO> history = Collections.singletonList(historyItem);

        when(accountRepository.findById(1L)).thenReturn(Mono.just(account));
        when(transactionServiceClient.getTransactionHistory(1L)).thenReturn(history);
        when(accountMapper.toResponseDTO(account)).thenReturn(accountResponseDTO);

        StepVerifier.create(accountService.findByIdWithHistory(1L))
                .expectNextMatches(result -> {
                    return result.getAccountInfo().equals(accountResponseDTO) &&
                           result.getTransactionHistory().equals(history);
                })
                .verifyComplete();
    }
}
