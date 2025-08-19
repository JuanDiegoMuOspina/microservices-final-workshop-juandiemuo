package com.banco.service.util;

import com.banco.dto.BankRequestDTO;
import com.banco.dto.BankResponseDTO;
import com.banco.model.Bank;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class BankMapperTest {

    private BankMapper bankMapper;

    @BeforeEach
    void setUp() {
        bankMapper = new BankMapper();
    }

    @Test
    @DisplayName("toEntity_shouldMapBankRequestDTOToBank")
    void toEntity_shouldMapBankRequestDTOToBank() {
        BankRequestDTO requestDTO = BankRequestDTO.builder()
                .name("Banco de Prueba")
                .code("BP001")
                .build();

        Bank bank = bankMapper.toEntity(requestDTO);

        assertNotNull(bank);
        assertEquals(requestDTO.getName(), bank.getName());
        assertEquals(requestDTO.getCode(), bank.getCode());
    }

    @Test
    @DisplayName("toResponseDTO_shouldMapBankToBankResponseDTO")
    void toResponseDTO_shouldMapBankToBankResponseDTO() {
        Bank bank = new Bank();
        bank.setId(1L);
        bank.setName("Banco de Prueba");
        bank.setCode("BP001");

        BankResponseDTO responseDTO = bankMapper.toResponseDTO(bank);

        assertNotNull(responseDTO);
        assertEquals(bank.getId(), responseDTO.getId());
        assertEquals(bank.getName(), responseDTO.getName());
        assertEquals(bank.getCode(), responseDTO.getCode());
    }
}
