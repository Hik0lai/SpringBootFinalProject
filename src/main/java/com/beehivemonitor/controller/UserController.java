package com.beehivemonitor.controller;

import com.beehivemonitor.dto.AuthResponse;
import com.beehivemonitor.security.JwtTokenProvider;
import com.beehivemonitor.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:5173")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @GetMapping("/me")
    public ResponseEntity<AuthResponse.UserResponse> getCurrentUser(@RequestHeader("Authorization") String token) {
        String email = tokenProvider.getEmailFromToken(token.substring(7));
        return ResponseEntity.ok(userService.getCurrentUser(email));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AuthResponse.UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/names")
    public ResponseEntity<List<UserNameResponse>> getAllUserNames(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(userService.getAllUserNames());
    }

    @PutMapping("/me/email-notifications")
    public ResponseEntity<AuthResponse.UserResponse> updateEmailNotificationPreference(
            @RequestHeader("Authorization") String token,
            @RequestBody EmailNotificationRequest request) {
        String email = tokenProvider.getEmailFromToken(token.substring(7));
        userService.updateEmailNotificationPreference(email, request.enabled);
        return ResponseEntity.ok(userService.getCurrentUser(email));
    }

    public static class EmailNotificationRequest {
        public Boolean enabled;
    }

    public static class UserNameResponse {
        public Long id;
        public String name;
        public String email;

        public UserNameResponse(Long id, String name, String email) {
            this.id = id;
            this.name = name;
            this.email = email;
        }
    }
}

