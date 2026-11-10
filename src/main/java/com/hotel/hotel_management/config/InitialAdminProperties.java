package com.hotel.hotel_management.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties (prefix = "app.initial-admin")
public record InitialAdminProperties(String username, String email, String password) {
    
    @Override
    public String toString() {
        return "InitialAdminProperties[username=" + username + ", email=" + email + "]";
    }

}
