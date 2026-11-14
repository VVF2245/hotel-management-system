package com.hotel.hotel_management.config;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties (prefix = "app.jwt")
public record JwtProperties(String secret, Duration expiration, String issuer) {
    
    @Override
    public String toString() {
        return "JwtProperties[expiration=" + 
            expiration + ", issuer=" + issuer + "]";
    }

}
