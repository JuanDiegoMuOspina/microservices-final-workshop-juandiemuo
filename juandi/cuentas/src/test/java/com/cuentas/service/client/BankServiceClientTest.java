package com.cuentas.service.client;

import com.cuentas.service.exception.BankNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BankServiceClientTest {

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private ClientResponse clientResponse;

    @InjectMocks
    private BankServiceClient bankServiceClient;

    @BeforeEach
    void setUp() {
        // Configuración del mock de WebClient para la cadena de llamadas fluida
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(String.class), any(Long.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.exchangeToMono(any())).thenAnswer(invocation -> {
            // Permite que la lambda pasada a exchangeToMono sea ejecutada con nuestro mock de ClientResponse
            return invocation.getArgument(0, java.util.function.Function.class).apply(clientResponse);
        });
    }

    @Test
    @DisplayName("validateBankExists_whenBankExists_shouldCompleteSuccessfully")
    void validateBankExists_whenBankExists_shouldCompleteSuccessfully() {
        // Arrange
        when(clientResponse.statusCode()).thenReturn(HttpStatus.OK);
        when(clientResponse.releaseBody()).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(bankServiceClient.validateBankExists(1L))
                .verifyComplete();
    }

    @Test
    @DisplayName("validateBankExists_whenBankNotFound_shouldThrowBankNotFoundException")
    void validateBankExists_whenBankNotFound_shouldThrowBankNotFoundException() {
        // Arrange
        when(clientResponse.statusCode()).thenReturn(HttpStatus.NOT_FOUND);

        // Act & Assert
        StepVerifier.create(bankServiceClient.validateBankExists(1L))
                .expectError(BankNotFoundException.class)
                .verify();
    }

    @Test
    @DisplayName("validateBankExists_whenApiReturnsError_shouldThrowRuntimeException")
    void validateBankExists_whenApiReturnsError_shouldThrowRuntimeException() {
        // Arrange
        when(clientResponse.statusCode()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
        when(clientResponse.bodyToMono(String.class)).thenReturn(Mono.just("Internal Server Error"));

        // Act & Assert
        StepVerifier.create(bankServiceClient.validateBankExists(1L))
                .expectError(RuntimeException.class)
                .verify();
    }
}