package com.apigateway.config;

import com.apigateway.security.JwtAuthenticationFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    private final JwtAuthenticationFilter filter;

    public GatewayConfig(JwtAuthenticationFilter filter) {
        this.filter = filter;
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("bank_service_route", r -> r.path("/api/v1/banks/**")
                        .filters(gtf -> gtf.filter(filter))
                        .uri("http://localhost:8081"))
                .route("account_service_route", r -> r.path("/api/v1/accounts/**")
                        .filters(gtf -> gtf.filter(filter))
                        .uri("http://localhost:8082"))
                .route("auth-service", route -> route.path("/api/auth/**").uri("http://localhost:8086"))
                .build();
    }
}
