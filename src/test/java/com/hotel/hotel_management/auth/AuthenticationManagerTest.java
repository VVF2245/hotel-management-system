package com.hotel.hotel_management.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.hotel.hotel_management.config.SecurityConfiguration;
import com.hotel.hotel_management.user.AccountStatus;
import com.hotel.hotel_management.user.Receptionist;
import com.hotel.hotel_management.user.User;
import com.hotel.hotel_management.user.UserRepository;

@ExtendWith(MockitoExtension.class)
public class AuthenticationManagerTest {

    @Mock
    private UserRepository userRepository;

    private final PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    private AuthenticationManager authenticationManager;

    @BeforeEach
    void setUp() {
        authenticationManager = new SecurityConfiguration().authenticationManager(
            new DatabaseUserDetailsService(userRepository), passwordEncoder);
    }

    private User receptionist(AccountStatus status) {
        User user = new Receptionist();
        user.setUsername("reception1");

        user.setPasswordHash(passwordEncoder.encode("Secret123"));
        user.setAccountStatus(status);

        return user;
    }

    private Authentication login(String username, String password) {
        return authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken.unauthenticated(username, password));
    }
    
    @Test
    void validCredentialsAuthenticateWithRole() {
        User user = receptionist(AccountStatus.ACTIVE);

        when(userRepository.findByUsername("reception1"))
            .thenReturn(Optional.of(user));
        
        Authentication result = login("reception1", "Secret123");

        assertThat(result.isAuthenticated()).isTrue();

        assertThat(result.isAuthenticated()).isTrue();

        assertThat(result.getAuthorities()).extracting("authority")
            .contains("ROLE_RECEPTIONIST");
        assertThat(((AuthenticatedUser) result.getPrincipal()).getUser()).isSameAs(user);
    }

    @Test
    void unknownUserIsRejectedLikeWrongPassword() {
        when(userRepository.findByUsername("ghost"))
            .thenReturn(Optional.empty());

        assertThatThrownBy(() -> login("ghost", "Secret123"))
            .isInstanceOf(BadCredentialsException.class);
    }

    @Test
    void inactiveAccountIsRejected() {
        when(userRepository.findByUsername("reception1"))
            .thenReturn(Optional.of(receptionist(AccountStatus.INACTIVE)));

        assertThatThrownBy(() -> login("reception1", "Secret123"))
            .isInstanceOf(DisabledException.class);
    }
}
