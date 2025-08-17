package com.cuentas.service.client;

import com.cuentas.service.exception.BankNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class BankServiceClient {

    private final WebClient webClient;

    public Mono<Void> validateBankExists(Long bankId) {
        return webClient.get()
                .uri("/api/v1/banks/{bankId}", bankId)
                .exchangeToMono(response -> {
                    if (response.statusCode().isSameCodeAs(HttpStatus.NOT_FOUND)) {
                        return Mono.error(new BankNotFoundException("El banco con id " + bankId + " no existe."));
                    }
                    if (response.statusCode().isError()) {
                        // Es importante consumir la respuesta incluso en caso de error para liberar la conexión.
                        return response.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(new RuntimeException("Error inesperado al validar el banco. Status: " + response.statusCode() + ", Body: " + body)));
                    }
                    // Si la respuesta es exitosa (2xx), liberamos el cuerpo y completamos el Mono.
                    // Esto es crucial para evitar fugas de conexión.
                    return response.releaseBody();
                });
    }
}
