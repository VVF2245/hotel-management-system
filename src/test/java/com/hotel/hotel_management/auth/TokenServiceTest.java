package com.hotel.hotel_management.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Duration;
import java.util.Base64;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;

import com.hotel.hotel_management.config.JwtConfiguration;
import com.hotel.hotel_management.config.JwtProperties;
import com.hotel.hotel_management.user.Administrator;
import com.hotel.hotel_management.user.User;

class TokenServiceTest {
    
    private static final String SECRET = Base64.getEncoder().encodeToString(new byte[32]);

    private final JwtProperties properties = new JwtProperties(SECRET, Duration.ofHours(1), "hotel-management");
    private final JwtConfiguration configuration = new JwtConfiguration();

    private TokenService tokenService;
    private  JwtDecoder decoder;

    @BeforeEach
    void setUp() {
        tokenService = new TokenService(configuration.jwtEncoder(properties), properties);
        decoder = configuration.jwtDecoder(properties);
    }

    @Test
    void issuedTokenContainsUserClaimsAndExpiry() {
        User user = new Administrator();
        user.setUsername("admin");
        user.setMustChangePassword(true);

        IssuedToken issued = tokenService.issue(user);
        Jwt jwt = decoder.decode(issued.value());

        assertThat(jwt.getSubject()).isEqualTo("admin");

        assertThat(jwt.getClaimAsStringList(TokenService.ROLES_CLAIM)).containsExactly("ADMINISTRATOR");
        assertThat(jwt.<Boolean>getClaim(TokenService.MUST_CHANGE_PASSWORD_CLAIM)).isTrue();

        assertThat(jwt.getClaimAsString("iss")).isEqualTo("hotel-management");

        assertThat(jwt.getExpiresAt()).isEqualTo(issued.expiresAt());

        assertThat(Duration.between(jwt.getIssuedAt(), jwt.getExpiresAt())).isEqualTo(Duration.ofHours(1));
    }

    @Test
    void tokenSignedWithAnotherKeyIsReejcted() {
        User user = new Administrator();
        user.setUsername("admin");
        String token = tokenService.issue(user).value();

        byte[] otherKey = new byte[32];
        otherKey[0] = 1;
        JwtProperties otherProperties = new JwtProperties(
            Base64.getEncoder().encodeToString(otherKey),
            Duration.ofHours(1), "hotel-management");
        JwtDecoder otherDecoder = configuration.jwtDecoder(otherProperties);

        assertThatThrownBy(() -> otherDecoder.decode(token)).isInstanceOf(JwtException.class);
    }
}
