package com.security.repository;


import com.security.model.User;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface IUserRepository extends ReactiveCrudRepository<User, Long> {
  Mono<User> findByUsername(String username);
}