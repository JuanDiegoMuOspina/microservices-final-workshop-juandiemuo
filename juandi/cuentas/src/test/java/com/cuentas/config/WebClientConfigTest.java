package com.cuentas.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Prueba unitaria para {@link WebClientConfig}.
 * Esta prueba verifica que el bean de WebClient se configure correctamente
 * sin levantar el contexto de Spring, enfocándose en la lógica de la clase.
 */
@ExtendWith(MockitoExtension.class)
class WebClientConfigTest {

    // La clase bajo prueba.
    @InjectMocks
    private WebClientConfig webClientConfig;

    // Mocks para simular la cadena de construcción de WebClient (Builder -> WebClient).
    @Mock
    private WebClient.Builder webClientBuilder;

    @Mock
    private WebClient webClient;

    // Captor para verificar el argumento pasado al método baseUrl.
    @Captor
    private ArgumentCaptor<String> baseUrlCaptor;

    private final String testUrl = "http://test-bank-service.com";

    @BeforeEach
    void setUp() {
        // Dado que @Value no funciona en una prueba unitaria sin contexto,
        // inyectamos manualmente el valor de la URL usando ReflectionTestUtils.
        ReflectionTestUtils.setField(webClientConfig, "bankServiceUrl", testUrl);
    }

    @Test
    void webClient_shouldBeConfiguredWithCorrectBaseUrl() {
        // Arrange
        // Mockeamos el método estático WebClient.builder() para que devuelva nuestro mock.
        // Esto nos permite controlar la cadena de llamadas (builder -> baseUrl -> build)
        // y aislar nuestra clase de la implementación real de WebClient.
        try (MockedStatic<WebClient> mockedStaticWebClient = Mockito.mockStatic(WebClient.class)) {
            // Cuando se llame a WebClient.builder(), devolvemos nuestro mock del builder.
            mockedStaticWebClient.when(WebClient::builder).thenReturn(webClientBuilder);

            // Configuramos el mock del builder para que al llamar a baseUrl(), devuelva el mismo builder.
            // Usamos un captor para verificar el valor con el que se llama.
            when(webClientBuilder.baseUrl(baseUrlCaptor.capture())).thenReturn(webClientBuilder);

            // Y cuando se llame a build(), devuelva nuestro mock final de WebClient.
            when(webClientBuilder.build()).thenReturn(webClient);

            // Act
            // Llamamos al método que queremos probar.
            WebClient result = webClientConfig.webClient();

            // Assert
            // 1. Verificamos que el resultado no es nulo.
            assertNotNull(result, "El WebClient no debería ser nulo.");

            // 2. Verificamos que el método estático WebClient.builder() fue invocado.
            mockedStaticWebClient.verify(WebClient::builder);

            // 3. Verificamos que el método baseUrl() fue llamado en el builder
            //    con la URL que inyectamos para la prueba.
            assertEquals(testUrl, baseUrlCaptor.getValue(), "La URL base configurada no es la esperada.");

            // 4. Verificamos que el método build() fue llamado al final para construir el objeto.
            verify(webClientBuilder).build();
        }
    }
}
