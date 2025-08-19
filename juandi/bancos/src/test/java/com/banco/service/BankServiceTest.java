package com.banco.service;

import com.banco.model.Bank;
import com.banco.dto.BankRequestDTO;
import com.banco.dto.BankResponseDTO;
import com.banco.repository.BankRepository;
import com.banco.service.util.BankMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BankServiceTest {

    @Mock
    private BankRepository bankRepository;

    @Mock
    private BankMapper bankMapper;

    @InjectMocks
    private BankService bankService;

    private Bank bank;
    private BankResponseDTO bankResponseDTO;
    private BankRequestDTO bankRequestDTO;

    @BeforeEach
    void setUp() {
        bank = new Bank();
        bank.setId(1L);
        bank.setName("Banco de Prueba");
        bank.setCode("BP001");

        bankResponseDTO = BankResponseDTO.builder()
                .id(1L)
                .name("Banco de Prueba")
                .code("BP001")
                .build();

        bankRequestDTO = BankRequestDTO.builder()
                .name("Banco Actualizado")
                .code("BPA002")
                .build();
    }

    @Test
    @DisplayName("findAll_shouldReturnFluxOfBankResponseDTO")
    void findAll_shouldReturnFluxOfBankResponseDTO() {
        when(bankRepository.findAll()).thenReturn(Flux.just(bank));
        when(bankMapper.toResponseDTO(bank)).thenReturn(bankResponseDTO);

        StepVerifier.create(bankService.findAll())
                .expectNext(bankResponseDTO)
                .verifyComplete();
    }

    @Test
    @DisplayName("findById_whenBankExists_shouldReturnBankResponseDTO")
    void findById_whenBankExists_shouldReturnBankResponseDTO() {
        when(bankRepository.findById(1L)).thenReturn(Mono.just(bank));
        when(bankMapper.toResponseDTO(bank)).thenReturn(bankResponseDTO);

        StepVerifier.create(bankService.findById(1L))
                .expectNext(bankResponseDTO)
                .verifyComplete();
    }

    @Test
    @DisplayName("findById_whenBankDoesNotExist_shouldReturnEmptyMono")
    void findById_whenBankDoesNotExist_shouldReturnEmptyMono() {
        when(bankRepository.findById(anyLong())).thenReturn(Mono.empty());

        StepVerifier.create(bankService.findById(1L))
                .verifyComplete();
    }

    @Test
    @DisplayName("findByCode_whenBankExists_shouldReturnBankResponseDTO")
    void findByCode_whenBankExists_shouldReturnBankResponseDTO() {
        when(bankRepository.findByCode("BP001")).thenReturn(Mono.just(bank));
        when(bankMapper.toResponseDTO(bank)).thenReturn(bankResponseDTO);

        StepVerifier.create(bankService.findByCode("BP001"))
                .expectNext(bankResponseDTO)
                .verifyComplete();
    }

    @Test
    @DisplayName("findByName_whenBankExists_shouldReturnBankResponseDTO")
    void findByName_whenBankExists_shouldReturnBankResponseDTO() {
        when(bankRepository.findByName("Banco de Prueba")).thenReturn(Mono.just(bank));
        when(bankMapper.toResponseDTO(bank)).thenReturn(bankResponseDTO);

        StepVerifier.create(bankService.findByName("Banco de Prueba"))
                .expectNext(bankResponseDTO)
                .verifyComplete();
    }

    @Test
    @DisplayName("create_shouldSaveAndReturnBankResponseDTO")
    void create_shouldSaveAndReturnBankResponseDTO() {
        when(bankMapper.toEntity(any(BankRequestDTO.class))).thenReturn(bank);
        when(bankRepository.save(bank)).thenReturn(Mono.just(bank));
        when(bankMapper.toResponseDTO(bank)).thenReturn(bankResponseDTO);

        StepVerifier.create(bankService.create(bankRequestDTO))
                .expectNext(bankResponseDTO)
                .verifyComplete();
    }

    @Test
    @DisplayName("update_whenBankExists_shouldUpdateAndReturnBankResponseDTO")
    void update_whenBankExists_shouldUpdateAndReturnBankResponseDTO() {
        when(bankRepository.findById(1L)).thenReturn(Mono.just(bank));
        when(bankRepository.save(any(Bank.class))).thenReturn(Mono.just(bank));
        when(bankMapper.toResponseDTO(bank)).thenReturn(bankResponseDTO);

        StepVerifier.create(bankService.update(1L, bankRequestDTO))
                .expectNext(bankResponseDTO)
                .verifyComplete();
    }

    @Test
    @DisplayName("update_whenBankDoesNotExist_shouldReturnEmptyMono")
    void update_whenBankDoesNotExist_shouldReturnEmptyMono() {
        when(bankRepository.findById(1L)).thenReturn(Mono.empty());

        StepVerifier.create(bankService.update(1L, bankRequestDTO))
                .verifyComplete();
    }

    @Test
    @DisplayName("deleteById_shouldCompleteSuccessfully")
    void deleteById_shouldCompleteSuccessfully() {
        when(bankRepository.deleteById(1L)).thenReturn(Mono.empty());

        StepVerifier.create(bankService.deleteById(1L))
                .verifyComplete();
    }
}
