package com.cuentas.controller;

import com.cuentas.dto.*;
import com.cuentas.service.AccountService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@WebFluxTest(AccountController.class)
class AccountControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private AccountService accountService;

    @Test
    void create_whenValidRequest_shouldReturnCreated() {

        CreateAccountRequestDTO request = new CreateAccountRequestDTO("123456789", "SAVINGS", 1L);
        AccountResponseDTO response = new AccountResponseDTO(1L, "123456789", "SAVINGS", BigDecimal.valueOf(1000), "ACTIVE", 1L);

        when(accountService.create(any(CreateAccountRequestDTO.class))).thenReturn(Mono.just(response));

        webTestClient.post().uri("/api/v1/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(AccountResponseDTO.class)
                .isEqualTo(response);
    }

    @Test
    void findById_whenAccountExists_shouldReturnAccount() {
        long accountId = 1L;
        AccountResponseDTO response = new AccountResponseDTO(accountId, "123456789", "SAVINGS", BigDecimal.valueOf(1000), "ACTIVE", 1L);

        when(accountService.findById(accountId)).thenReturn(Mono.just(response));

        webTestClient.get().uri("/api/v1/accounts/{id}", accountId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(AccountResponseDTO.class)
                .isEqualTo(response);
    }



    @Test
    void findByAccountNumber_whenAccountExists_shouldReturnAccount() {
        String accountNumber = "987654321";
        AccountResponseDTO response = new AccountResponseDTO(2L, accountNumber, "CHECKING", BigDecimal.valueOf(500), "ACTIVE", 2L);

        when(accountService.findByAccountNumber(accountNumber)).thenReturn(Mono.just(response));

        webTestClient.get().uri("/api/v1/accounts/number/{accountNumber}", accountNumber)
                .exchange()
                .expectStatus().isOk()
                .expectBody(AccountResponseDTO.class)
                .isEqualTo(response);
    }

    @Test
    void findByBankId_whenAccountsExist_shouldReturnAccountList() {
        long bankId = 1L;
        List<AccountResponseDTO> accounts = List.of(
                new AccountResponseDTO(1L, "111", "SAVINGS", BigDecimal.TEN, "ACTIVE", bankId),
                new AccountResponseDTO(2L, "222", "CHECKING", BigDecimal.ONE, "ACTIVE", bankId)
        );
        when(accountService.findByBankId(bankId)).thenReturn(Flux.fromIterable(accounts));

        webTestClient.get().uri("/api/v1/accounts/bank/{bankId}", bankId)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(AccountResponseDTO.class)
                .isEqualTo(accounts);
    }

    @Test
    void updateStatus_whenValidRequest_shouldReturnUpdatedAccount() {
        long accountId = 1L;
        UpdateAccountStatusRequestDTO request = new UpdateAccountStatusRequestDTO("INACTIVE");
        AccountResponseDTO response = new AccountResponseDTO(accountId, "123456789", "SAVINGS", BigDecimal.valueOf(1000), "INACTIVE", 1L);

        when(accountService.updateStatus(eq(accountId), any(UpdateAccountStatusRequestDTO.class))).thenReturn(Mono.just(response));

        webTestClient.patch().uri("/api/v1/accounts/{id}/status", accountId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody(AccountResponseDTO.class)
                .isEqualTo(response);
    }

    @Test
    void createTransfer_whenValidRequest_shouldReturnTransactionResponse() {
        // Corregido: Se usan Long en lugar de String para los IDs de cuenta.
        TransferRequestDTO request = new TransferRequestDTO(123L, 456L, BigDecimal.valueOf(100));
        // Corregido: Se añade el timestamp nulo para cumplir con el constructor.
        ProcessTransactionResponseDTO response = new ProcessTransactionResponseDTO("some-transaction-id", "COMPLETED", null);

        when(accountService.createTransfer(any(TransferRequestDTO.class))).thenReturn(Mono.just(response));

        webTestClient.post().uri("/api/v1/accounts/transfers")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ProcessTransactionResponseDTO.class)
                .isEqualTo(response);
    }

    @Test
    void findByIdWithHistory_whenAccountExists_shouldReturnAccountWithHistory() {
        long accountId = 1L;
        // Corregido: Se instancia el DTO anidado AccountResponseDTO primero.
        AccountResponseDTO accountInfo = new AccountResponseDTO(accountId, "123", "SAVINGS", BigDecimal.TEN, "ACTIVE", 1L);
        AccountWithHistoryDTO response = new AccountWithHistoryDTO(accountInfo, List.of());

        when(accountService.findByIdWithHistory(accountId)).thenReturn(Mono.just(response));

        webTestClient.get().uri("/api/v1/accounts/{id}/history", accountId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(AccountWithHistoryDTO.class)
                .isEqualTo(response);
    }

    @Test
    void create_whenInvalidBody_shouldReturnBadRequest() {
        // Corregido: Se usa el constructor correcto para crear una solicitud inválida.
        CreateAccountRequestDTO invalidRequest = new CreateAccountRequestDTO("", null, null);

        webTestClient.post().uri("/api/v1/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(invalidRequest)
                .exchange()
                .expectStatus().isBadRequest();
    }
}
