package com.hotel.hotel_management.auth;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import com.hotel.hotel_management.config.JwtProperties;
import com.hotel.hotel_management.user.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TokenService {
    
    public static final String ROLES_CLAIM = "roles";
    public static final String MUST_CHANGE_PASSWORD_CLAIM = "mustChangePassword";

    private final JwtEncoder jwtEncoder;
    private final JwtProperties properties;

    public IssuedToken issue(User user) {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        Instant expiresAt = now.plus(properties.expiration());

        JwtClaimsSet claims = JwtClaimsSet.builder()
            .issuer(properties.issuer())
            .issuedAt(now)
            .expiresAt(expiresAt)
            .subject(user.getUsername())
            .claim(ROLES_CLAIM, List.of(user.getRole().name()))
            .claim(MUST_CHANGE_PASSWORD_CLAIM, user.isMustChangePassword())
            .build();

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        String token = jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();

        return new IssuedToken(token, expiresAt);
    }
}
