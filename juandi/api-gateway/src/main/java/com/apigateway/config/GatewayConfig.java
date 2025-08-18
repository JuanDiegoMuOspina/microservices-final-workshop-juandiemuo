package com.apigateway.config;

import com.apigateway.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    private final JwtAuthenticationFilter filter;

    @Value("${services.uris.bank-service}")
    private String bankServiceUri;

    @Value("${services.uris.account-service}")
    private String accountServiceUri;

    @Value("${services.uris.security-service}")
    private String securityServiceUri;

    @Value("${routes.bank-service.id}")
    private String bankServiceId;

    @Value("${routes.bank-service.path}")
    private String bankServicePath;

    @Value("${routes.account-service.id}")
    private String accountServiceId;

    @Value("${routes.account-service.path}")
    private String accountServicePath;

    @Value("${routes.security-service.id}")
    private String securityServiceId;

    @Value("${routes.security-service.path}")
    private String securityServicePath;

    public GatewayConfig(JwtAuthenticationFilter filter) {
        this.filter = filter;
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(bankServiceId, r -> r.path(bankServicePath)
                        .filters(gtf -> gtf.filter(filter))
                        .uri(bankServiceUri))
                .route(accountServiceId, r -> r.path(accountServicePath)
                        .filters(gtf -> gtf.filter(filter))
                        .uri(accountServiceUri))
                .route(securityServiceId, route -> route.path(securityServicePath)
                        .uri(securityServiceUri))
                .build();
    }
}
