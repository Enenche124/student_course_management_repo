package com.learning.config;

import com.learning.data.models.Role;
import com.learning.data.models.User;
import com.learning.data.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class StartupRunner {

    @Bean
    public CommandLineRunner seedAdmin(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        return args -> {
            String defaultAdminEmail = "enenchejohn56@gmail.com";
            String defaultAdminPassword = "Enenche@2002";

            if (userRepository.findByEmail(defaultAdminEmail).isEmpty()) {
                User admin = new User();
                admin.setName("Super Admin");
                admin.setEmail(defaultAdminEmail);
                admin.setPassword(passwordEncoder.encode(defaultAdminPassword));
                admin.setRole(Role.ADMIN);
                admin.setCreatedBy("SYSTEM");
                admin.setSuperAdmin(true);

                userRepository.save(admin);
                System.out.println("✅ Default Super Admin seeded");
            } else {
                System.out.println("🔐 Super Admin already exists");
            }
        };
    }
}
