
package com.islamnizami.fleettrack.config;

import com.islamnizami.fleettrack.model.entity.AppUser;
import com.islamnizami.fleettrack.model.enums.UserRole;
import com.islamnizami.fleettrack.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SetupAdminCommand implements CommandLineRunner {

    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ConfigurableApplicationContext context;

    @Override
    public void run(String... args) {
        boolean setupRequested = java.util.Arrays.asList(args)
                .contains("setup-admin");

        if (!setupRequested) {
            return;
        }

        try {
            String username = System.getenv("ADMIN_USERNAME");
            String password = System.getenv("ADMIN_PASSWORD");

            if (username == null || username.isBlank()
                    || password == null || password.length() < 12) {
                throw new IllegalStateException(
                        "Set ADMIN_USERNAME and ADMIN_PASSWORD (at least 12 characters).");
            }

            if (userRepository.existsByUsername(username)) {
                System.out.println("Admin already exists. No changes made.");
                return;
            }

            AppUser admin = new AppUser();
            admin.setUsername(username);
            admin.setPassword(passwordEncoder.encode(password));
            admin.setRole(UserRole.ADMIN);

            userRepository.save(admin);
            System.out.println("Admin created successfully.");
        } finally {
            SpringApplication.exit(context, () -> 0);
        }
    }
}
