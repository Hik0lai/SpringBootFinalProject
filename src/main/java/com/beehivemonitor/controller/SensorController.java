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

    /**
     * Get real-time sensor data for a single hive using GET endpoint from microservice
     */
    @GetMapping("/realtime-data/hive/{hiveId}")
    public ResponseEntity<HiveSensorData> getRealtimeDataForHive(
            @PathVariable Long hiveId,
            @RequestHeader("Authorization") String token) {
        String email = getEmailFromToken(token);
        return ResponseEntity.ok(sensorService.getRealtimeSensorDataForHive(hiveId, email));
    }

    /**
     * Update all sensor data for all user's beehives by calling the sensor microservice
     * This endpoint explicitly triggers a refresh of sensor data from the microservice
     */
    @PostMapping("/update")
    public ResponseEntity<java.util.Map<String, Object>> updateAllSensors(
            @RequestHeader("Authorization") String token) {
        String email = getEmailFromToken(token);
        
        try {
            java.util.Map<Long, HiveSensorData> updatedData = sensorService.updateAllSensorData(email);
            
            java.util.Map<String, Object> response = new java.util.HashMap<>();
            int hiveCount = updatedData != null ? updatedData.size() : 0;
            
            if (hiveCount == 0) {
                response.put("message", "No beehives found. Please create a beehive first.");
                response.put("updatedHives", 0);
            } else {
                response.put("message", "Sensor data updated successfully for " + hiveCount + " beehive(s)");
                response.put("updatedHives", hiveCount);
            }
            response.put("sensorData", updatedData != null ? updatedData : new java.util.HashMap<>());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // Log the error for debugging
            System.err.println("Error updating sensors: " + e.getMessage());
            e.printStackTrace();
            
            // Return error response
            java.util.Map<String, Object> errorResponse = new java.util.HashMap<>();
            errorResponse.put("error", e.getMessage() != null ? e.getMessage() : "Failed to update sensors");
            errorResponse.put("message", "Failed to update sensors: " + (e.getMessage() != null ? e.getMessage() : "Unknown error"));
            return ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    public static class HiveSensorData {
        public double temperature;
        public double externalTemperature;
        public double humidity;
        public double co2;
        public double soundLevel;
        public double weight;

        public HiveSensorData(double temperature, double externalTemperature, double humidity, double co2, double soundLevel, double weight) {
            this.temperature = temperature;
            this.externalTemperature = externalTemperature;
            this.humidity = humidity;
            this.co2 = co2;
            this.soundLevel = soundLevel;
            this.weight = weight;
        }
    }
}

