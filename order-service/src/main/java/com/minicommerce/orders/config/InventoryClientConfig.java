package com.minicommerce.orders.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class InventoryClientConfig {

    @Bean
    public WebClient inventoryClient() {
        return WebClient.builder()
                .baseUrl("http://localhost:8083")
                .build();
    }
}
