package com.beehivemonitor.controller;

import com.beehivemonitor.entity.Alert;
import com.beehivemonitor.security.JwtTokenProvider;
import com.beehivemonitor.service.AlertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/alerts")
@CrossOrigin(origins = "http://localhost:5173")
public class AlertController {

    @Autowired
    private AlertService alertService;

    @Autowired
    private JwtTokenProvider tokenProvider;

    private String getEmailFromToken(String authHeader) {
        return tokenProvider.getEmailFromToken(authHeader.substring(7));
    }

    @GetMapping
    public ResponseEntity<List<AlertResponse>> getAllAlerts(@RequestHeader("Authorization") String token) {
        String email = getEmailFromToken(token);
        List<Alert> alerts = alertService.getAllAlertsByUser(email);
        
        List<AlertResponse> responses = alerts.stream()
            .map(alert -> new AlertResponse(
                alert.getId(),
                alert.getTitle(),
                alert.getMessage(),
                alert.getCreatedAt().toString()
            ))
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(responses);
    }

    public static class AlertResponse {
        public Long id;
        public String title;
        public String message;
        public String createdAt;

        public AlertResponse(Long id, String title, String message, String createdAt) {
            this.id = id;
            this.title = title;
            this.message = message;
            this.createdAt = createdAt;
        }
    }
}

