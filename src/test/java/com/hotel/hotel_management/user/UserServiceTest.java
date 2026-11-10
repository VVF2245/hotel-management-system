package com.hotel.hotel_management.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    private final PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository, passwordEncoder);
    }

    @Test
    void assignTemporaryPasswordStoresHashAndRequiresChange() {
        User user = new Receptionist();

        userService.assignTemporaryPassword(user, "TempPass1234");

        assertThat(user.getPasswordHash()).isNotEqualTo("TempPass1234");
        assertThat(passwordEncoder.matches("TempPass1234", user.getPasswordHash())).isTrue();
        assertThat(user.isMustChangePassword()).isTrue();
        verify(userRepository).save(user);
    }

    @Test
    void changePasswordWithWrongCurrentPasswordFailsAndDoesNotSave() {
        User user = new Receptionist();
        user.setPasswordHash(passwordEncoder.encode("CurrentPass1"));

        assertThatThrownBy(() -> userService.changePassword(user, "WrongPass1", "NewPass1234"))
                .isInstanceOf(IncorrectPasswordException.class);

        verify(userRepository, never()).save(any());
    }

}
