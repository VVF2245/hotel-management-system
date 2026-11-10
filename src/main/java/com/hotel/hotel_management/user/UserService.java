package com.hotel.hotel_management.user;

import java.security.SecureRandom;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private static final String TEMPORARY_PASSWORD_ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz23456789";

    private static final int TEMPORARY_PASSWORD_LENGTH = 12;

    private final SecureRandom secureRandom = new SecureRandom();
 

    @Transactional
    public void assignTemporaryPassword(User user, String temporaryPassword) {
        if (temporaryPassword == null || temporaryPassword.isBlank()) {
            throw new IllegalArgumentException("Temporary password cannot be null or blank");
        }

        user.setPasswordHash(passwordEncoder.encode(temporaryPassword));
        user.setMustChangePassword(true);
        userRepository.save(user);
    }

    @Transactional
    public void changePassword(User user, String currentPassword, String newPassword) {
        if (currentPassword == null || !passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
            throw new IncorrectPasswordException("Current password is incorrect");
        }
        if (newPassword == null || newPassword.isBlank()) {
            throw new IllegalArgumentException("New password cannot be null or blank");
        }

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setMustChangePassword(false);
        userRepository.save(user);
    }

    public String generateTemporaryPassword() {
        StringBuilder temporaryPassword = new StringBuilder(TEMPORARY_PASSWORD_LENGTH);
        for(int i = 0; i < TEMPORARY_PASSWORD_LENGTH; i++) {
            int index = secureRandom.nextInt(TEMPORARY_PASSWORD_ALPHABET.length());

            temporaryPassword.append(TEMPORARY_PASSWORD_ALPHABET.charAt(index));
        }

        return temporaryPassword.toString();
    }

    
}
