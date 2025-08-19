package com.security.controller;

import com.security.config.SecurityConfig;
import com.security.dto.AuthRequest;
import com.security.dto.AuthResponse;
import com.security.dto.RegisterRequest;
import com.security.model.User;
import com.security.service.JwtService;
import com.security.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

// Especificamos el controlador y AÑADIMOS @Import para cargar nuestra configuración de seguridad
@WebFluxTest(UserController.class)
@Import(SecurityConfig.class)
@DisplayName("UserController Tests")
class UserControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserService userService;

    @MockBean
    private PasswordEncoder passwordEncoder;

    // Mantenemos este MockBean. Al importar SecurityConfig, este mock reemplazará
    // el bean de ReactiveAuthenticationManager real, dándonos control total para la prueba del controlador.
    @MockBean
    private ReactiveAuthenticationManager authenticationManager;

    @MockBean
    private ReactiveUserDetailsService userDetailsService;

    @Test
    @DisplayName("login_withValidCredentials_shouldReturnOkWithToken")
    void login_withValidCredentials_shouldReturnOkWithToken() {
        // Arrange
        AuthRequest authRequest = new AuthRequest("testuser", "password");
        Authentication authentication = mock(Authentication.class);
        String dummyToken = "dummy.jwt.token";

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(Mono.just(authentication));
        when(jwtService.generateToken(authentication)).thenReturn(dummyToken);

        // Act & Assert
        webTestClient.post().uri("/api/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(authRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(AuthResponse.class)
                .isEqualTo(new AuthResponse(dummyToken));
    }

    @Test
    @DisplayName("login_withInvalidCredentials_shouldReturnUnauthorized")
    void login_withInvalidCredentials_shouldReturnUnauthorized() {
        // Arrange
        AuthRequest authRequest = new AuthRequest("wronguser", "wrongpassword");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(Mono.error(new BadCredentialsException("Invalid Credentials")));

        // Act & Assert
        webTestClient.post().uri("/api/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(authRequest)
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    @DisplayName("register_withValidData_shouldReturnOkWithUser")
    void register_withValidData_shouldReturnOkWithUser() {
        // Arrange
        RegisterRequest registerRequest = new RegisterRequest("newuser", "password", "newuser@example.com");
        User registeredUser = new User();
        registeredUser.setId(1L);
        registeredUser.setUsername("newuser");

        when(userService.register(any(RegisterRequest.class))).thenReturn(Mono.just(registeredUser));

        // Act & Assert
        webTestClient.post().uri("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(registerRequest)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    @DisplayName("register_whenUserAlreadyExists_shouldReturnError")
    void register_whenUserAlreadyExists_shouldReturnError() {
        // Arrange
        RegisterRequest registerRequest = new RegisterRequest("existinguser", "password", "existing@example.com");

        when(userService.register(any(RegisterRequest.class)))
                .thenReturn(Mono.error(new RuntimeException("User already exists")));

        // Act & Assert
        webTestClient.post().uri("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(registerRequest)
                .exchange()
                .expectStatus().is5xxServerError(); // Assuming a generic error handler
    }
}
