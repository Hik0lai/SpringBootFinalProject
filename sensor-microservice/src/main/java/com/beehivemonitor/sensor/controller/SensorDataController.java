package com.beehivemonitor.sensor.controller;

import com.beehivemonitor.sensor.dto.HiveSensorDataDTO;
import com.beehivemonitor.sensor.dto.RealtimeDataRequest;
import com.beehivemonitor.sensor.dto.RealtimeDataResponse;
import com.beehivemonitor.sensor.service.SensorDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sensor-data")
@CrossOrigin(origins = {"http://localhost:8080", "http://localhost:5173"})
public class SensorDataController {

    @Autowired
    private SensorDataService sensorDataService;

    /**
     * Generate sensor data for a single hive
     */
    @GetMapping("/hive/{hiveId}")
    public ResponseEntity<HiveSensorDataDTO> getSensorDataForHive(@PathVariable Long hiveId) {
        HiveSensorDataDTO data = sensorDataService.generateSensorData();
        return ResponseEntity.ok(data);
    }

    /**
     * Generate sensor data for multiple hives (POST endpoint)
     * This matches the main application's usage pattern
     */
    @PostMapping("/realtime")
    public ResponseEntity<RealtimeDataResponse> getRealtimeDataForHives(
            @RequestBody RealtimeDataRequest request) {
        List<Long> hiveIds = request.getHiveIds();
        Map<Long, HiveSensorDataDTO> sensorData = sensorDataService.generateSensorDataForHives(hiveIds);
        return ResponseEntity.ok(new RealtimeDataResponse(sensorData));
    }

    /**
     * Alternative GET endpoint for realtime data (for convenience)
     */
    @GetMapping("/realtime")
    public ResponseEntity<RealtimeDataResponse> getRealtimeDataForHivesGet(
            @RequestParam List<Long> hiveIds) {
        Map<Long, HiveSensorDataDTO> sensorData = sensorDataService.generateSensorDataForHives(hiveIds);
        return ResponseEntity.ok(new RealtimeDataResponse(sensorData));
    }
}


