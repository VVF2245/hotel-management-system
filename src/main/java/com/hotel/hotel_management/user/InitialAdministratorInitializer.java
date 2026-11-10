package com.hotel.hotel_management.user;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.hotel.hotel_management.config.InitialAdminProperties;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Creates the first administrator on startup when none exists, so the system can be accessed.
 * The initial password comes from configuration, is stored only as a hash, and must be changed
 * at first sign-in. It is never logged.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class InitialAdministratorInitializer implements ApplicationRunner {
    
    private final AdministratorRepository administratorRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final InitialAdminProperties properties;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (administratorRepository.count() > 0) {
            return;
        }
        if (properties.password() == null || properties.password().isBlank()) {
            throw new IllegalStateException("ADMIN_INITIAL_PASSWORD must be set to create the initial administrator");
        }
        if (userRepository.existsByUsername(properties.username())) {
            throw new IllegalStateException("Cannot create the initial administrator: username '" + properties.username() +
            "' is already in use");
        }

        Administrator administrator = new Administrator();

        administrator.setFirstName("System");
        administrator.setFirstName("System");
        administrator.setLastName("Administrator");
        administrator.setUsername(properties.username());
        administrator.setEmail(properties.email());

        userService.assignTemporaryPassword(administrator, properties.password());

        log.info("Initial administrator '{} created; password change required at first sign-in", properties.username());
    }
    
}
