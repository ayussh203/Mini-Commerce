package com.minicommerce.auth.api;

public record LoginResponse(
        String accessToken,
        String tokenType,
        long expiresIn
) {}
