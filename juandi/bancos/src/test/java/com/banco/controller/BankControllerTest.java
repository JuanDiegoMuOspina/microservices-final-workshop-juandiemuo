package com.banco.controller;

import com.banco.dto.BankRequestDTO;
import com.banco.dto.BankResponseDTO;
import com.banco.service.BankService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@WebFluxTest(BankController.class)
class BankControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private BankService bankService;

    private BankResponseDTO bankResponseDTO;
    private BankRequestDTO bankRequestDTO;

    @BeforeEach
    void setUp() {
        bankResponseDTO = BankResponseDTO.builder()
                .id(1L)
                .name("Banco de Prueba")
                .code("BP001")
                .build();

        bankRequestDTO = BankRequestDTO.builder()
                .name("Banco de Prueba")
                .code("BP001")
                .build();
    }

    @Test
    @DisplayName("findAll_shouldReturnAllBanks")
    void findAll_shouldReturnAllBanks() {
        when(bankService.findAll()).thenReturn(Flux.just(bankResponseDTO));

        webTestClient.get().uri("/api/v1/banks")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(BankResponseDTO.class)
                .hasSize(1)
                .contains(bankResponseDTO);
    }

    @Test
    @DisplayName("findById_whenBankExists_shouldReturnBank")
    void findById_whenBankExists_shouldReturnBank() {
        when(bankService.findById(1L)).thenReturn(Mono.just(bankResponseDTO));

        webTestClient.get().uri("/api/v1/banks/1")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(BankResponseDTO.class)
                .isEqualTo(bankResponseDTO);
    }



    @Test
    @DisplayName("findByCode_whenBankExists_shouldReturnBank")
    void findByCode_whenBankExists_shouldReturnBank() {
        when(bankService.findByCode("BP001")).thenReturn(Mono.just(bankResponseDTO));

        webTestClient.get().uri("/api/v1/banks/code/BP001")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(BankResponseDTO.class)
                .isEqualTo(bankResponseDTO);
    }

    @Test
    @DisplayName("findByName_whenBankExists_shouldReturnBank")
    void findByName_whenBankExists_shouldReturnBank() {
        when(bankService.findByName("Banco de Prueba")).thenReturn(Mono.just(bankResponseDTO));

        webTestClient.get().uri("/api/v1/banks/name/Banco de Prueba")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(BankResponseDTO.class)
                .isEqualTo(bankResponseDTO);
    }

    @Test
    @DisplayName("create_withValidData_shouldReturnCreatedBank")
    void create_withValidData_shouldReturnCreatedBank() {
        when(bankService.create(any(BankRequestDTO.class))).thenReturn(Mono.just(bankResponseDTO));

        webTestClient.post().uri("/api/v1/banks")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(bankRequestDTO), BankRequestDTO.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(BankResponseDTO.class)
                .isEqualTo(bankResponseDTO);
    }

    @Test
    @DisplayName("update_whenBankExists_shouldReturnUpdatedBank")
    void update_whenBankExists_shouldReturnUpdatedBank() {
        when(bankService.update(eq(1L), any(BankRequestDTO.class))).thenReturn(Mono.just(bankResponseDTO));

        webTestClient.put().uri("/api/v1/banks/1")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(bankRequestDTO), BankRequestDTO.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(BankResponseDTO.class)
                .isEqualTo(bankResponseDTO);
    }



    @Test
    @DisplayName("deleteById_whenBankExists_shouldReturnNoContent")
    void deleteById_whenBankExists_shouldReturnNoContent() {
        when(bankService.deleteById(1L)).thenReturn(Mono.empty());

        webTestClient.delete().uri("/api/v1/banks/1")
                .exchange()
                .expectStatus().isNoContent();
    }


}