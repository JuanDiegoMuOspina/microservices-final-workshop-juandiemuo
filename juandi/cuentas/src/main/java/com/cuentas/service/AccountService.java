package com.cuentas.service;

import com.cuentas.dto.AccountResponseDTO;
import com.cuentas.dto.CreateAccountRequestDTO;
import com.cuentas.dto.UpdateAccountStatusRequestDTO;
import com.cuentas.model.Account;
import com.cuentas.repository.AccountRepository;
import com.cuentas.service.client.BankServiceClient;
import com.cuentas.service.exception.AccountAlreadyExistsException;
import com.cuentas.service.util.AccountMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final BankServiceClient bankServiceClient;
    private final AccountMapper accountMapper;

    public Mono<AccountResponseDTO> create(CreateAccountRequestDTO requestDTO) {
        // Componemos un Mono<Void> que valida si la cuenta ya existe.
        Mono<Void> checkAccountExists = accountRepository.findByAccountNumber(requestDTO.getAccountNumber())
                .flatMap(existingAccount -> Mono.error(new AccountAlreadyExistsException("La cuenta con el número " + requestDTO.getAccountNumber() + " ya existe.")))
                .then();

        // Componemos la cadena reactiva completa.
        return bankServiceClient.validateBankExists(requestDTO.getBankId()) // 1. Validar que el banco existe.
                .then(checkAccountExists) // 2. Si el banco existe, validar que la cuenta no exista.
                .then(Mono.defer(() -> { // 3. Si ambas validaciones pasan, proceder a crear la cuenta.
                    Account newAccount = accountMapper.toEntity(requestDTO);
                    // La lógica de negocio principal establece los valores iniciales.
                    newAccount.setBalance(BigDecimal.ZERO);
                    newAccount.setStatus("ACTIVE");
                    return accountRepository.save(newAccount);
                }))
                .map(accountMapper::toResponseDTO); // 4. Mapear la entidad guardada a un DTO de respuesta.
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
}
