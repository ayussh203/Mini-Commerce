package com.minicommerce.apigateway.filter;

import com.minicommerce.apigateway.jwt.GatewayJwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthFilter implements GlobalFilter, Ordered {

    private final GatewayJwtService jwt;

    public JwtAuthFilter(GatewayJwtService jwt) {
        this.jwt = jwt;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        // PUBLIC: allow auth endpoints
        if (path.startsWith("/api/auth/")) {
            return chain.filter(exchange);
        }

        // PROTECTED: orders + inventory (as per US 1.2)
        boolean protectedRoute =
                path.startsWith("/api/orders/") ||
                path.startsWith("/api/inventory/");

        if (!protectedRoute) {
            return chain.filter(exchange);
        }

        String auth = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (auth == null || !auth.startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String token = auth.substring(7);
        try {
            Claims claims = jwt.parse(token);

            // (optional) you can forward claims to downstream via headers later
            // e.g. X-User, X-Roles, etc.

            return chain.filter(exchange);
        } catch (JwtException e) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }

    @Override
    public int getOrder() {
        return -50; // after correlation filter, before routing
    }
}
