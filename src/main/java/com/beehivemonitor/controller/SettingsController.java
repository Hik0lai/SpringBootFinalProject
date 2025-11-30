package com.beehivemonitor.controller;

import com.beehivemonitor.entity.UserSettings;
import com.beehivemonitor.security.JwtTokenProvider;
import com.beehivemonitor.service.SettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/settings")
@CrossOrigin(origins = "http://localhost:5173")
public class SettingsController {

    @Autowired
    private SettingsService settingsService;

    @Autowired
    private JwtTokenProvider tokenProvider;

    private String getEmailFromToken(String authHeader) {
        return tokenProvider.getEmailFromToken(authHeader.substring(7));
    }

    @GetMapping
    public ResponseEntity<SettingsResponse> getSettings(@RequestHeader("Authorization") String token) {
        String email = getEmailFromToken(token);
        UserSettings settings = settingsService.getSettingsByEmail(email);
        return ResponseEntity.ok(new SettingsResponse(settings.getMeasurementIntervalMinutes()));
    }

    @PutMapping
    public ResponseEntity<Map<String, String>> updateSettings(
            @RequestBody SettingsRequest request,
            @RequestHeader("Authorization") String token) {
        String email = getEmailFromToken(token);
        settingsService.updateMeasurementInterval(email, request.measurementIntervalMinutes);
        return ResponseEntity.ok(Map.of("message", "Change is saved"));
    }

    public static class SettingsRequest {
        public Integer measurementIntervalMinutes;
    }

    public static class SettingsResponse {
        public Integer measurementIntervalMinutes;

        public SettingsResponse(Integer measurementIntervalMinutes) {
            this.measurementIntervalMinutes = measurementIntervalMinutes;
        }
    }
}
