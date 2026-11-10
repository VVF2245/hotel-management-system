package com.hotel.hotel_management.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
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

    private static final String TEMPORARY_PASSWORD_ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz23456789";

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

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource (strings = {"   ", "\t"})
    void assignTemporaryPasswordRejectsBlankAndDoesNotSave(String temporaryPassword) {
        User user = new Receptionist();

        assertThatThrownBy(() -> userService.assignTemporaryPassword(user, temporaryPassword))
            .isInstanceOf(IllegalArgumentException.class);

        assertThat(user.getPasswordHash()).isNull();
        verify(userRepository, never()).save(any());
    }

    @Test
    void changePasswordWithWrongCurrentPasswordFailsAndDoesNotSave() {
        User user = new Receptionist();
        user.setPasswordHash(passwordEncoder.encode("CurrentPass1"));

        assertThatThrownBy(() -> userService.changePassword(user, "WrongPass1", "NewPass1234"))
                .isInstanceOf(IncorrectPasswordException.class);

        verify(userRepository, never()).save(any());
    }

    @Test
    void changePasswordWithCorrectCurrentPasswordStoresNewHashClearsFlag() {
        User user = new Receptionist();

        user.setPasswordHash(passwordEncoder.encode("CurrentPass1"));
        user.setMustChangePassword(true);

        userService.changePassword(user, "CurrentPass1", "NewPass1234");

        assertThat(passwordEncoder.matches("NewPass1234", user.getPasswordHash())).isTrue();

        assertThat(passwordEncoder.matches("CurrentPass1", user.getPasswordHash())).isFalse();
        assertThat(user.isMustChangePassword()).isFalse();

        verify(userRepository).save(user);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource (strings = {"   "})
    void changePasswordRejectsBlankNewPasswordAndKeepsOldHash(String newPassword) {
        User user = new Receptionist();
        String originalHash = passwordEncoder.encode("CurrentPass1");
        user.setPasswordHash(originalHash);
        user.setMustChangePassword(true);

        assertThatThrownBy(() -> userService.changePassword(user, "CurrentPass1", newPassword))
            .isInstanceOf(IllegalArgumentException.class);

        assertThat(user.getPasswordHash()).isEqualTo(originalHash);
        assertThat(user.isMustChangePassword()).isTrue();

        verify(userRepository, never()).save(any());
    }


    @Test
    void generateTemporaryPasswordHasTwelveCharacters() {
        assertThat(userService.generateTemporaryPassword()).hasSize(12);
    }

    @Test
    void generateTemporaryPasswordUsesOnlyAlphabetCharacters() {
        String password = userService.generateTemporaryPassword();

        for (char character : password.toCharArray()) {
            assertThat(TEMPORARY_PASSWORD_ALPHABET).contains(String.valueOf(character));
        }
        assertThat(password).doesNotContain("0", "O", "1", "l", "I");
    }

    @Test
    void generateTemporaryPasswordReturnsDifferentValues() {
        
        assertThat(userService.generateTemporaryPassword())
            .isNotEqualTo(userService.generateTemporaryPassword());
    }

}
