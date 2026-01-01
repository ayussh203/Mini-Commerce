package com.minicommerce.apigateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
public class CorrelationIdFilter implements GlobalFilter, Ordered {

    private static final String HEADER = "X-Correlation-Id";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String cid = exchange.getRequest().getHeaders().getFirst(HEADER);
        if (cid == null || cid.isBlank()) {
            cid = UUID.randomUUID().toString();
            ServerHttpRequest mutated = exchange.getRequest()
                    .mutate()
                    .header(HEADER, cid)
                    .build();
            exchange = exchange.mutate().request(mutated).build();
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -100; // early
    }
}
