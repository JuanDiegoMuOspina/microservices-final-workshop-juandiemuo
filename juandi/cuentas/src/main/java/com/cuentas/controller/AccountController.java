package com.cuentas.controller;

import com.cuentas.dto.AccountResponseDTO;
import com.cuentas.dto.CreateAccountRequestDTO;
import com.cuentas.dto.UpdateAccountStatusRequestDTO;
import com.cuentas.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<AccountResponseDTO> create(@Valid @RequestBody CreateAccountRequestDTO requestDTO) {
        return accountService.create(requestDTO);
    }

    @GetMapping("/{id}")
    public Mono<AccountResponseDTO> findById(@PathVariable Long id) {
        return accountService.findById(id);
    }

    @GetMapping("/number/{accountNumber}")
    public Mono<AccountResponseDTO> findByAccountNumber(@PathVariable String accountNumber) {
        return accountService.findByAccountNumber(accountNumber);
    }

    @GetMapping("/bank/{bankId}")
    public Flux<AccountResponseDTO> findByBankId(@PathVariable Long bankId) {
        return accountService.findByBankId(bankId);
    }

    @PatchMapping("/{id}/status")
    public Mono<AccountResponseDTO> updateStatus(@PathVariable Long id, @Valid @RequestBody UpdateAccountStatusRequestDTO requestDTO) {
        return accountService.updateStatus(id, requestDTO);
    }

    // TODO: Endpoint pendiente para obtener una cuenta con su historial de movimientos.
    // Requerirá la implementación del cliente gRPC para comunicarse con el servicio de transacciones.
}
