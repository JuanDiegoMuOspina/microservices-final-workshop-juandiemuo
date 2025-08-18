package com.transferencias.repository;

import com.transferencias.model.Transaction;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends R2dbcRepository<Transaction, Long> {
}
