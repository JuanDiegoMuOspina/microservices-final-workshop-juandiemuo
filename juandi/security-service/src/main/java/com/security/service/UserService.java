package com.security.service;


import com.security.dto.RegisterRequest;
import com.security.model.User;
import com.security.repository.IUserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class UserService {
  private final IUserRepository repository;
  private final PasswordEncoder encoder;

  public UserService(IUserRepository repository, PasswordEncoder encoder) {
    this.repository = repository;
    this.encoder = encoder;
  }

  public Mono<Object> register(RegisterRequest request) {
    return repository.findByUsername(request.username())
      .flatMap(existing -> Mono.error(new RuntimeException("User already exists")))
      .switchIfEmpty(repository.save(new User(null, request.username(), encoder.encode(request.password()), request.role())));
  }
}
