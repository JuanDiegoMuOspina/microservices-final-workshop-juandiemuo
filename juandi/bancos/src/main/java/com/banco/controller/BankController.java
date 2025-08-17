package com.banco.controller;

import com.banco.dto.BankRequestDTO;
import com.banco.dto.BankResponseDTO;
import com.banco.service.BankService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;



@RestController
@RequestMapping("/api/v1/banks")
@RequiredArgsConstructor
public class BankController {

    private final BankService bankService;

    @GetMapping
    public Flux<BankResponseDTO> findAll() {
        return bankService.findAll();
    }

    @GetMapping("/{id}")
    public Mono<BankResponseDTO> findById(@PathVariable Long id) {
        return bankService.findById(id);
    }

    @GetMapping("/code/{code}")
    public Mono<BankResponseDTO> findByCode(@PathVariable String code) {
        return bankService.findByCode(code);
    }

    @GetMapping("/name/{name}")
    public Mono<BankResponseDTO> findByName(@PathVariable String name) {
        return bankService.findByName(name);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<BankResponseDTO> create(@Valid @RequestBody BankRequestDTO requestDTO) {
        return bankService.create(requestDTO);
    }

    @PutMapping("/{id}")
    public Mono<BankResponseDTO> update(@PathVariable Long id, @Valid @RequestBody BankRequestDTO requestDTO) {
        return bankService.update(id, requestDTO);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteById(@PathVariable Long id) {
        return bankService.deleteById(id);
    }
}
