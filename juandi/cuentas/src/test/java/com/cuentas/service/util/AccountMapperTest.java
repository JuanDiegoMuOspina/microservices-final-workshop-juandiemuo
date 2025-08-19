package com.cuentas.service.util;

import com.cuentas.dto.AccountResponseDTO;
import com.cuentas.dto.CreateAccountRequestDTO;
import com.cuentas.model.Account;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class AccountMapperTest {

    private AccountMapper accountMapper;

    @BeforeEach
    void setUp() {
        accountMapper = new AccountMapper();
    }

    @Test
    void toEntity_whenGivenCreateAccountRequestDTO_shouldMapToAccountEntity() {
        // Arrange
        CreateAccountRequestDTO dto = new CreateAccountRequestDTO();
        dto.setAccountNumber("123456789");
        dto.setAccountType("SAVINGS");
        dto.setBankId(1L);

        // Act
        Account account = accountMapper.toEntity(dto);

        // Assert
        assertNotNull(account);
        assertEquals(dto.getAccountNumber(), account.getAccountNumber());
        assertEquals(dto.getAccountType(), account.getAccountType());
        assertEquals(dto.getBankId(), account.getBankId());
        assertEquals(BigDecimal.ZERO, account.getBalance());
        assertEquals("ACTIVE", account.getStatus());
    }

    @Test
    void toResponseDTO_whenGivenAccountEntity_shouldMapToAccountResponseDTO() {
        // Arrange
        Account account = new Account();
        account.setId(1L);
        account.setAccountNumber("123456789");
        account.setAccountType("SAVINGS");
        account.setBalance(new BigDecimal("1000.00"));
        account.setStatus("ACTIVE");
        account.setBankId(1L);

        // Act
        AccountResponseDTO dto = accountMapper.toResponseDTO(account);

        // Assert
        assertNotNull(dto);
        assertEquals(account.getId(), dto.getId());
        assertEquals(account.getAccountNumber(), dto.getAccountNumber());
        assertEquals(account.getAccountType(), dto.getAccountType());
        assertEquals(account.getBalance(), dto.getBalance());
        assertEquals(account.getStatus(), dto.getStatus());
        assertEquals(account.getBankId(), dto.getBankId());
    }
}
