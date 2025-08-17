package com.banco.service.util;

import com.banco.dto.BankRequestDTO;
import com.banco.dto.BankResponseDTO;
import com.banco.model.Bank;
import org.springframework.stereotype.Component;

@Component
public class BankMapper {

    public Bank toEntity(BankRequestDTO dto) {
        Bank bank = new Bank();
        bank.setName(dto.getName());
        bank.setCode(dto.getCode());
        return bank;
    }

    public BankResponseDTO toResponseDTO(Bank bank) {
        BankResponseDTO dto = new BankResponseDTO();
        dto.setId(bank.getId());
        dto.setName(bank.getName());
        dto.setCode(bank.getCode());
        return dto;
    }
}
