package com.security.service;

import com.security.dto.RegisterRequest;
import com.security.model.User;
import com.security.repository.IUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private IUserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void register_whenUserDoesNotExist_shouldSaveAndReturnUser() {
        // Arrange
        RegisterRequest request = new RegisterRequest("newuser", "password123", "USER");
        User savedUser = new User(1L, "newuser", "encodedPassword", "USER");

        when(userRepository.findByUsername("newuser")).thenReturn(Mono.empty());
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(Mono.just(savedUser));

        // Act
        Mono<Object> result = userService.register(request);

        // Assert
        StepVerifier.create(result)
                .expectNext(savedUser)
                .verifyComplete();

        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userArgumentCaptor.capture());

        User userToSave = userArgumentCaptor.getValue();
        assertEquals("newuser", userToSave.getUsername());
        assertEquals("encodedPassword", userToSave.getPassword());
        assertEquals("USER", userToSave.getRole());
    }


}
