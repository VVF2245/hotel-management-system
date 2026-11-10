package com.hotel.hotel_management.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.hotel.hotel_management.config.InitialAdminProperties;

@ExtendWith (MockitoExtension.class)
public class InitialAdministratorInitializerTest {
    
    @Mock
    private AdministratorRepository administratorRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserService userService;

    private InitialAdministratorInitializer initializerWith(String password) {
        InitialAdminProperties properties = new InitialAdminProperties("admin", "admin@hotel.local", password);

        return new InitialAdministratorInitializer(administratorRepository, userRepository, userService, properties);
    }

    @Test
    void doesNothingWhenAnAdministratorAlreadyExists() {
        when(administratorRepository.count()).thenReturn(1L);

        initializerWith("InitialPass1").run(null);

        verifyNoInteractions(userService);
    }

    @Test
    void createsAdministratorWithTemporaryPasswordWhenNoneExists() {
        when(administratorRepository.count()).thenReturn(0L);
        when(userRepository.existsByUsername("admin")).thenReturn(false);

        initializerWith("InitialPass1").run(null);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userService).assignTemporaryPassword(captor.capture(), eq("InitialPass1"));
        User created = captor.getValue();
        assertThat(created).isInstanceOf(Administrator.class);
        assertThat(created.getUsername()).isEqualTo("admin");
        assertThat(created.getEmail()).isEqualTo("admin@hotel.local");
    }

    @Test
    void failsWhenInitialPasswordIsMissing() {
        when(administratorRepository.count()).thenReturn(0L);

        assertThatThrownBy(() -> initializerWith("").run(null))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("ADMIN_INITIAL_PASSWORD");
        verifyNoInteractions(userService);
    }

    @Test
    void failsWhenUsernameIsAlreadyTaken() {
        when(administratorRepository.count()).thenReturn(0L);
        when(userRepository.existsByUsername("admin")).thenReturn(true);

        assertThatThrownBy(() -> initializerWith("InitialPass1").run(null))
                .isInstanceOf(IllegalStateException.class);
        verify(userService, org.mockito.Mockito.never()).assignTemporaryPassword(any(), any());
    }

    
}
