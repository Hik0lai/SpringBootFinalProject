package com.beehivemonitor.controller;

import com.beehivemonitor.dto.SensorReadingDTO;
import com.beehivemonitor.security.JwtTokenProvider;
import com.beehivemonitor.service.SensorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sensors")
@CrossOrigin(origins = "http://localhost:5173")
public class SensorController {

    @Autowired
    private SensorService sensorService;

    @Autowired
    private JwtTokenProvider tokenProvider;

    private String getEmailFromToken(String authHeader) {
        return tokenProvider.getEmailFromToken(authHeader.substring(7));
    }

    @GetMapping("/last-readings")
    public ResponseEntity<List<SensorReadingDTO>> getLastReadings(@RequestParam Long hiveId,
                                                                   @RequestHeader("Authorization") String token) {
        String email = getEmailFromToken(token);
        return ResponseEntity.ok(sensorService.getLatestReadingsByHiveId(hiveId, email));
    }

    @GetMapping("/realtime-data")
    public ResponseEntity<java.util.Map<Long, HiveSensorData>> getRealtimeDataForAllHives(
            @RequestHeader("Authorization") String token) {
        String email = getEmailFromToken(token);
        return ResponseEntity.ok(sensorService.getRealtimeDataForAllHives(email));
    }

    public static class HiveSensorData {
        public double temperature;
        public double humidity;
        public double co2;
        public double soundLevel;

        public HiveSensorData(double temperature, double humidity, double co2, double soundLevel) {
            this.temperature = temperature;
            this.humidity = humidity;
            this.co2 = co2;
            this.soundLevel = soundLevel;
        }
    }
}

