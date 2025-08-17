package com.banco.service;

import com.banco.dto.BankRequestDTO;
import com.banco.dto.BankResponseDTO;
import com.banco.repository.BankRepository;
import com.banco.service.util.BankMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class BankService {

    private final BankRepository bankRepository;
    private final BankMapper bankMapper;

    public Flux<BankResponseDTO> findAll() {
        return bankRepository.findAll()
                .map(bankMapper::toResponseDTO);
    }

    public Mono<BankResponseDTO> findById(Long id) {
        return bankRepository.findById(id)
                .map(bankMapper::toResponseDTO);
    }

    public Mono<BankResponseDTO> findByCode(String code) {
        return bankRepository.findByCode(code)
                .map(bankMapper::toResponseDTO);
    }

    public Mono<BankResponseDTO> findByName(String name) {
        return bankRepository.findByName(name)
                .map(bankMapper::toResponseDTO);
    }

    public Mono<BankResponseDTO> create(BankRequestDTO requestDTO) {
        return Mono.just(requestDTO)
                .map(bankMapper::toEntity)
                .flatMap(bankRepository::save)
                .map(bankMapper::toResponseDTO);
    }

    public Mono<BankResponseDTO> update(Long id, BankRequestDTO requestDTO) {
        return bankRepository.findById(id)
                .flatMap(bank -> {
                    bank.setName(requestDTO.getName());
                    bank.setCode(requestDTO.getCode());
                    return bankRepository.save(bank);
                })
                .map(bankMapper::toResponseDTO);
    }

    public Mono<Void> deleteById(Long id) {
        return bankRepository.deleteById(id);
    }
}
