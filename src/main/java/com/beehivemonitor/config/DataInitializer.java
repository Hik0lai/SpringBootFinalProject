package com.beehivemonitor.config;

import com.beehivemonitor.entity.User;
import com.beehivemonitor.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${admin.default.name:Nikolay Kolev}")
    private String adminName;

    @Value("${admin.default.email:vef@abv.bg}")
    private String adminEmail;

    @Value("${admin.default.password:admin123}")
    private String adminPassword;

    @Override
    public void run(String... args) throws Exception {
        // Check if any users exist
        if (userRepository.count() == 0) {
            System.out.println("==========================================");
            System.out.println("No users found in database. Creating default admin user...");
            System.out.println("==========================================");

            User admin = new User();
            admin.setName(adminName);
            admin.setEmail(adminEmail);
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setRole(User.Role.ADMIN);
            admin.setEmailNotificationEnabled(false);

            userRepository.save(admin);

            System.out.println("Default admin user created successfully!");
            System.out.println("Name: " + adminName);
            System.out.println("Email: " + adminEmail);
            System.out.println("Password: " + adminPassword);
            System.out.println("==========================================");
            System.out.println("IMPORTANT: Please change the default password after first login!");
            System.out.println("==========================================");
        } else {
            System.out.println("Users already exist in database. Skipping default admin creation.");
        }
    }
}


