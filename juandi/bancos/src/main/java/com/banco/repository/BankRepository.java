package com.banco.repository;

import com.banco.model.Bank;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface BankRepository extends ReactiveCrudRepository<Bank, Long> {

    Mono<Bank> findByCode(String code);

    Mono<Bank> findByName(String name);
}
