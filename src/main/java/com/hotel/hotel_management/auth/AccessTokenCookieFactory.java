package com.hotel.hotel_management.auth;

import java.time.Duration;
import java.time.Instant;

import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import com.hotel.hotel_management.config.AuthCookieProperties;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AccessTokenCookieFactory {

    private static final String PATH = "/api";

    private final AuthCookieProperties properties;

    public ResponseCookie create(IssuedToken token) {
        Duration maxAge = Duration.between(Instant.now(), token.expiresAt());
        return base(token.value()).maxAge(maxAge).build();
    }

    public ResponseCookie clear() {
        return base("").maxAge(Duration.ZERO).build();
    }

    private ResponseCookie.ResponseCookieBuilder base(String value) {
        return ResponseCookie.from(properties.name(), value)
                .httpOnly(true)
                .secure(properties.secure())
                .sameSite("Strict")
                .path(PATH);
    }

}