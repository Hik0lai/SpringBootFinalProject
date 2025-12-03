package com.beehivemonitor.controller;

import com.beehivemonitor.entity.Alert;
import com.beehivemonitor.security.JwtTokenProvider;
import com.beehivemonitor.service.AlertService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
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
                alert.getName(),
                alert.getHive().getId(),
                alert.getHive().getName(),
                alert.getTriggerConditions(),
                alert.getIsTriggered() != null ? alert.getIsTriggered() : false,
                alert.getCreatedAt().toString()
            ))
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AlertResponse> getAlertById(@PathVariable UUID id, 
                                                       @RequestHeader("Authorization") String token) {
        String email = getEmailFromToken(token);
        Alert alert = alertService.getAlertById(id, email);
        return ResponseEntity.ok(new AlertResponse(
            alert.getId(),
            alert.getName(),
            alert.getHive().getId(),
            alert.getHive().getName(),
            alert.getTriggerConditions(),
            alert.getIsTriggered() != null ? alert.getIsTriggered() : false,
            alert.getCreatedAt().toString()
        ));
    }

    @PostMapping
    public ResponseEntity<AlertResponse> createAlert(@Valid @RequestBody AlertRequest request,
                                                     @RequestHeader("Authorization") String token) {
        String email = getEmailFromToken(token);
        
        Alert alert = new Alert();
        alert.setName(request.name);
        alert.setHive(new com.beehivemonitor.entity.Hive());
        alert.getHive().setId(request.hiveId);
        alert.setTriggerConditions(request.triggerConditions);
        
        alert = alertService.createAlert(alert, email);
        return ResponseEntity.ok(new AlertResponse(
            alert.getId(),
            alert.getName(),
            alert.getHive().getId(),
            alert.getHive().getName(),
            alert.getTriggerConditions(),
            alert.getIsTriggered() != null ? alert.getIsTriggered() : false,
            alert.getCreatedAt().toString()
        ));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AlertResponse> updateAlert(@PathVariable UUID id,
                                                      @Valid @RequestBody AlertRequest request,
                                                      @RequestHeader("Authorization") String token) {
        String email = getEmailFromToken(token);
        
        Alert alert = new Alert();
        alert.setName(request.name);
        alert.setHive(new com.beehivemonitor.entity.Hive());
        alert.getHive().setId(request.hiveId);
        alert.setTriggerConditions(request.triggerConditions);
        
        alert = alertService.updateAlert(id, alert, email);
        return ResponseEntity.ok(new AlertResponse(
            alert.getId(),
            alert.getName(),
            alert.getHive().getId(),
            alert.getHive().getName(),
            alert.getTriggerConditions(),
            alert.getIsTriggered() != null ? alert.getIsTriggered() : false,
            alert.getCreatedAt().toString()
        ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAlert(@PathVariable UUID id,
                                             @RequestHeader("Authorization") String token) {
        String email = getEmailFromToken(token);
        alertService.deleteAlert(id, email);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/reset")
    public ResponseEntity<AlertResponse> resetAlert(@PathVariable UUID id,
                                                     @RequestHeader("Authorization") String token) {
        String email = getEmailFromToken(token);
        Alert alert = alertService.resetAlert(id, email);
        return ResponseEntity.ok(new AlertResponse(
            alert.getId(),
            alert.getName(),
            alert.getHive().getId(),
            alert.getHive().getName(),
            alert.getTriggerConditions(),
            alert.getIsTriggered() != null ? alert.getIsTriggered() : false,
            alert.getCreatedAt().toString()
        ));
    }

    public static class AlertRequest {
        public String name;
        public UUID hiveId;
        public String triggerConditions; // JSON string
    }

    public static class AlertResponse {
        public UUID id;
        public String name;
        public UUID hiveId;
        public String hiveName;
        public String triggerConditions;
        public Boolean isTriggered;
        public String createdAt;

        public AlertResponse(UUID id, String name, UUID hiveId, String hiveName, 
                           String triggerConditions, Boolean isTriggered, String createdAt) {
            this.id = id;
            this.name = name;
            this.hiveId = hiveId;
            this.hiveName = hiveName;
            this.triggerConditions = triggerConditions;
            this.isTriggered = isTriggered;
            this.createdAt = createdAt;
        }
    }
}

