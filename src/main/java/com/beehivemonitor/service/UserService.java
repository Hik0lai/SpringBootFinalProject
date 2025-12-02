package com.beehivemonitor.service;

import com.beehivemonitor.dto.AuthResponse;
import com.beehivemonitor.entity.User;
import com.beehivemonitor.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public AuthResponse.UserResponse getCurrentUser(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
        return AuthResponse.UserResponse.fromUser(user);
    }

    public List<AuthResponse.UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
            .map(AuthResponse.UserResponse::fromUser)
            .collect(Collectors.toList());
    }

    public List<com.beehivemonitor.controller.UserController.UserNameResponse> getAllUserNames() {
        return userRepository.findAll().stream()
            .map(user -> new com.beehivemonitor.controller.UserController.UserNameResponse(
                user.getId(),
                user.getName(),
                user.getEmail()
            ))
            .collect(Collectors.toList());
    }

    public void updateEmailNotificationPreference(String email, Boolean enabled) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
        user.setEmailNotificationEnabled(enabled);
        userRepository.save(user);
    }

    public AuthResponse.UserResponse updateUserRole(Long userId, User.Role newRole, String adminEmail) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Prevent admin from removing their own admin role
        User adminUser = userRepository.findByEmail(adminEmail)
            .orElseThrow(() -> new RuntimeException("Admin user not found"));
        
        if (adminUser.getId().equals(userId) && user.getRole() == User.Role.ADMIN && newRole == User.Role.USER) {
            throw new RuntimeException("You cannot remove admin role from yourself. Please ask another admin to do it.");
        }
        
        user.setRole(newRole);
        user = userRepository.save(user);
        return AuthResponse.UserResponse.fromUser(user);
    }

    @Transactional
    public void changePassword(String email, String currentPassword, String newPassword) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

        // Validate and trim current password
        if (currentPassword == null || currentPassword.trim().isEmpty()) {
            throw new RuntimeException("Current password is required");
        }
        currentPassword = currentPassword.trim();

        // Validate and trim new password
        if (newPassword == null || newPassword.trim().isEmpty()) {
            throw new RuntimeException("New password cannot be empty");
        }
        newPassword = newPassword.trim();

        // Verify current password
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            throw new RuntimeException("User password is not set. Please contact administrator.");
        }

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new RuntimeException("Current password is incorrect. Please verify your current password and try again.");
        }

        // Validate new password length
        if (newPassword.length() < 6) {
            throw new RuntimeException("New password must be at least 6 characters long");
        }

        // Check if new password is different from current password
        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new RuntimeException("New password must be different from your current password");
        }

        // Set new password
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}

