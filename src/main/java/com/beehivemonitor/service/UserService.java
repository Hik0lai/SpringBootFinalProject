package com.beehivemonitor.service;

import com.beehivemonitor.dto.AuthResponse;
import com.beehivemonitor.entity.User;
import com.beehivemonitor.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

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
}

