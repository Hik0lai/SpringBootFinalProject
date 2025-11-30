package com.beehivemonitor.controller;

import com.beehivemonitor.entity.HiveSensorData;
import com.beehivemonitor.security.JwtTokenProvider;
import com.beehivemonitor.service.SensorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/graphics")
@CrossOrigin(origins = "http://localhost:5173")
public class GraphicsController {

    @Autowired
    private SensorService sensorService;

    @Autowired
    private JwtTokenProvider tokenProvider;

    private String getEmailFromToken(String authHeader) {
        return tokenProvider.getEmailFromToken(authHeader.substring(7));
    }

    @GetMapping("/historical-data")
    public ResponseEntity<List<HiveSensorDataResponse>> getHistoricalData(
            @RequestParam Long hiveId,
            @RequestParam int days,
            @RequestHeader("Authorization") String token) {
        String email = getEmailFromToken(token);
        
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusDays(days);
        
        List<HiveSensorData> data = sensorService.getHistoricalData(hiveId, startDate, endDate, email);
        
        List<HiveSensorDataResponse> responses = data.stream()
            .map(item -> new HiveSensorDataResponse(
                item.getId(),
                item.getHive().getId(),
                item.getTemperature(),
                item.getHumidity(),
                item.getCo2(),
                item.getSoundLevel(),
                item.getWeight(),
                item.getTimestamp().toString()
            ))
            .collect(java.util.stream.Collectors.toList());
        
        return ResponseEntity.ok(responses);
    }

    public static class HiveSensorDataResponse {
        public Long id;
        public Long hiveId;
        public Double temperature;
        public Double humidity;
        public Double co2;
        public Double soundLevel;
        public Double weight;
        public String timestamp;

        public HiveSensorDataResponse(Long id, Long hiveId, Double temperature, Double humidity,
                                     Double co2, Double soundLevel, Double weight, String timestamp) {
            this.id = id;
            this.hiveId = hiveId;
            this.temperature = temperature;
            this.humidity = humidity;
            this.co2 = co2;
            this.soundLevel = soundLevel;
            this.weight = weight;
            this.timestamp = timestamp;
        }
    }
}

