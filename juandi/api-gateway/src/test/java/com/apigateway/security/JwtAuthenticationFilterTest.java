package com.apigateway.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private ServerWebExchange exchange;

    @Mock
    private GatewayFilterChain chain;

    @Mock
    private ServerHttpRequest request;

    @Mock
    private ServerHttpResponse response;

    @Mock
    private HttpHeaders headers;

    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private final String jwtPrefix = "Bearer ";

    @BeforeEach
    void setUp() {
        jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtService, jwtPrefix);
        when(exchange.getRequest()).thenReturn(request);
        when(exchange.getResponse()).thenReturn(response);
    }



    @Test
    @DisplayName("filter_whenNoAuthorizationHeader_shouldReturnUnauthorized")
    void filter_whenNoAuthorizationHeader_shouldReturnUnauthorized() {
        when(request.getHeaders()).thenReturn(headers);
        when(headers.containsKey(HttpHeaders.AUTHORIZATION)).thenReturn(false);
        when(response.setComplete()).thenReturn(Mono.empty());

        StepVerifier.create(jwtAuthenticationFilter.filter(exchange, chain))
                .verifyComplete();

        verify(response).setStatusCode(HttpStatus.UNAUTHORIZED);
        verify(chain, never()).filter(exchange);
    }

    @Test
    @DisplayName("filter_whenAuthorizationHeaderIsInvalid_shouldReturnUnauthorized")
    void filter_whenAuthorizationHeaderIsInvalid_shouldReturnUnauthorized() {
        when(request.getHeaders()).thenReturn(headers);
        when(headers.containsKey(HttpHeaders.AUTHORIZATION)).thenReturn(true);
        when(headers.getFirst(HttpHeaders.AUTHORIZATION)).thenReturn("InvalidHeader");
        when(response.setComplete()).thenReturn(Mono.empty());

        StepVerifier.create(jwtAuthenticationFilter.filter(exchange, chain))
                .verifyComplete();

        verify(response).setStatusCode(HttpStatus.UNAUTHORIZED);
        verify(chain, never()).filter(exchange);
    }

    @Test
    @DisplayName("filter_whenTokenIsInvalid_shouldReturnUnauthorized")
    void filter_whenTokenIsInvalid_shouldReturnUnauthorized() {
        String token = "invalid-token";
        when(request.getHeaders()).thenReturn(headers);
        when(headers.containsKey(HttpHeaders.AUTHORIZATION)).thenReturn(true);
        when(headers.getFirst(HttpHeaders.AUTHORIZATION)).thenReturn(jwtPrefix + token);
        when(jwtService.isValidToken(token)).thenReturn(false);
        when(response.setComplete()).thenReturn(Mono.empty());

        StepVerifier.create(jwtAuthenticationFilter.filter(exchange, chain))
                .verifyComplete();

        verify(response).setStatusCode(HttpStatus.UNAUTHORIZED);
        verify(chain, never()).filter(exchange);
    }

    @Test
    @DisplayName("filter_whenTokenValidationThrowsException_shouldReturnUnauthorized")
    void filter_whenTokenValidationThrowsException_shouldReturnUnauthorized() {
        String token = "exception-token";
        when(request.getHeaders()).thenReturn(headers);
        when(headers.containsKey(HttpHeaders.AUTHORIZATION)).thenReturn(true);
        when(headers.getFirst(HttpHeaders.AUTHORIZATION)).thenReturn(jwtPrefix + token);
        when(jwtService.isValidToken(token)).thenThrow(new RuntimeException("Validation error"));
        when(response.setComplete()).thenReturn(Mono.empty());

        StepVerifier.create(jwtAuthenticationFilter.filter(exchange, chain))
                .verifyComplete();

        verify(response).setStatusCode(HttpStatus.UNAUTHORIZED);
        verify(chain, never()).filter(exchange);
    }
}
