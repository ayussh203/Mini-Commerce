package com.minicommerce.orders.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class InventoryClientConfig {

    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

    @Bean
    public WebClient inventoryClient(WebClient.Builder builder) {
        return builder.baseUrl("http://localhost:8083").build();
    }
}
