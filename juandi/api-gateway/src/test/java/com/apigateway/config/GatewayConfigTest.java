package com.apigateway.config;

import com.apigateway.security.JwtAuthenticationFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.server.WebFilterChain;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
        "services.uris.bank-service=http://localhost:9991",
        "services.uris.account-service=http://localhost:9992",
        "services.uris.security-service=http://localhost:9993",
        "routes.bank-service.id=bank-service",
        "routes.bank-service.path=/api/v1/banks/**",
        "routes.account-service.id=account-service",
        "routes.account-service.path=/api/v1/accounts/**",
        "routes.security-service.id=security-service",
        "routes.security-service.path=/api/v1/security/**"
})
class GatewayConfigTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @BeforeEach
    void setUp() {
        when(jwtAuthenticationFilter.filter(any(), any(GatewayFilterChain.class)))
                .thenAnswer(invocation -> invocation.getArgument(1, WebFilterChain.class)
                        .filter(invocation.getArgument(0)));
    }

    @Test
    @DisplayName("requestToBankServicePath_shouldRouteToBankService")
    void requestToBankServicePath_shouldRouteToBankService() {
        webTestClient.get().uri("/api/v1/banks/1")
                .exchange()
                .expectStatus().isEqualTo(500);
    }

    @Test
    @DisplayName("requestToAccountServicePath_shouldRouteToAccountService")
    void requestToAccountServicePath_shouldRouteToAccountService() {
        webTestClient.get().uri("/api/v1/accounts/1")
                .exchange()
                .expectStatus().isEqualTo(500);
    }

    @Test
    @DisplayName("requestToSecurityServicePath_shouldRouteToSecurityService")
    void requestToSecurityServicePath_shouldRouteToSecurityService() {
        webTestClient.post().uri("/api/v1/security/login")
                .exchange()
                .expectStatus().isEqualTo(500);
    }

    @Test
    @DisplayName("requestToUnknownPath_shouldReturnNotFound")
    void requestToUnknownPath_shouldReturnNotFound() {
        webTestClient.get().uri("/api/v1/unknown")
                .exchange()
                .expectStatus().isNotFound();
    }
}
