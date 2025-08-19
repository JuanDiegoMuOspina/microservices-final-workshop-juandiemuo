package com.security.service;

import com.security.model.User;
import com.security.repository.IUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class UserDetailServiceTest {

    @Mock
    private IUserRepository userRepository;

    @InjectMocks
    private UserDetailService userDetailService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findByUsername_whenUserExists_shouldReturnUserDetails() {
        // Arrange
        User mockUser = new User();
        mockUser.setUsername("testuser");
        mockUser.setPassword("password");
        mockUser.setRole("USER");

        when(userRepository.findByUsername("testuser")).thenReturn(Mono.just(mockUser));

        // Act
        Mono<UserDetails> userDetailsMono = userDetailService.findByUsername("testuser");

        // Assert
        StepVerifier.create(userDetailsMono)
                .expectNextMatches(userDetails ->
                        userDetails.getUsername().equals("testuser") &&
                        userDetails.getPassword().equals("password") &&
                        userDetails.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_USER"))
                )
                .verifyComplete();
    }

    @Test
    void findByUsername_whenUserDoesNotExist_shouldReturnEmptyMono() {
        // Arrange
        when(userRepository.findByUsername(anyString())).thenReturn(Mono.empty());

        // Act
        Mono<UserDetails> userDetailsMono = userDetailService.findByUsername("nonexistent");

        // Assert
        StepVerifier.create(userDetailsMono)
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void findByUsername_whenRepositoryThrowsError_shouldPropagateError() {
        // Arrange
        RuntimeException exception = new RuntimeException("Database error");
        when(userRepository.findByUsername(anyString())).thenReturn(Mono.error(exception));

        // Act
        Mono<UserDetails> userDetailsMono = userDetailService.findByUsername("anyuser");

        // Assert
        StepVerifier.create(userDetailsMono)
                .expectError(RuntimeException.class)
                .verify();
    }
}
