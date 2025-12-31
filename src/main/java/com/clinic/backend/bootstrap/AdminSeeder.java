package com.clinic.backend.bootstrap;

import com.clinic.backend.user.Role;
import com.clinic.backend.user.User;
import com.clinic.backend.user.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminSeeder {

    @Bean
    CommandLineRunner seedAdmin(UserRepository repo, PasswordEncoder encoder) {
        return args -> {
            if (repo.findByUsernameAndActiveTrue("admin").isPresent()) return;

            User u = new User();
            u.setUsername("admin");
            u.setPasswordHash(encoder.encode("123456"));
            u.setRole(Role.ADMIN);
            u.setActive(true);

            repo.save(u);
            System.out.println("✅ Seeded admin user: admin / 123456");
        };
    }
}
