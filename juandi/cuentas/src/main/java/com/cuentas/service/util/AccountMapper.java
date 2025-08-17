package com.cuentas.service.util;

import com.cuentas.dto.AccountResponseDTO;
import com.cuentas.dto.CreateAccountRequestDTO;
import com.cuentas.model.Account;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class AccountMapper {

    public Account toEntity(CreateAccountRequestDTO dto) {
        Account account = new Account();
        account.setAccountNumber(dto.getAccountNumber());
        account.setAccountType(dto.getAccountType());
        account.setBankId(dto.getBankId());
        // El balance y el estado se establecen en la lógica de negocio del servicio
        account.setBalance(BigDecimal.ZERO); 
        account.setStatus("ACTIVE");
        return account;
    }

    public AccountResponseDTO toResponseDTO(Account account) {
        AccountResponseDTO dto = new AccountResponseDTO();
        dto.setId(account.getId());
        dto.setAccountNumber(account.getAccountNumber());
        dto.setAccountType(account.getAccountType());
        dto.setBalance(account.getBalance());
        dto.setStatus(account.getStatus());
        dto.setBankId(account.getBankId());
        return dto;
    }
}
