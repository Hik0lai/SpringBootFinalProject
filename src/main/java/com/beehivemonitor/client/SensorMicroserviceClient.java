package com.beehivemonitor.client;

import com.beehivemonitor.dto.MicroserviceRealtimeRequest;
import com.beehivemonitor.dto.MicroserviceRealtimeResponse;
import com.beehivemonitor.dto.MicroserviceSensorDataDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

/**
 * Feign Client for communicating with the Sensor Microservice
 */
@FeignClient(name = "sensor-microservice", url = "${sensor.microservice.url}")
public interface SensorMicroserviceClient {

    /**
     * Get real-time sensor data for multiple hives (POST)
     * 
     * @param request Request containing list of hive IDs
     * @return Response containing sensor data map keyed by hive ID
     */
    @PostMapping("/api/sensor-data/realtime")
    MicroserviceRealtimeResponse getRealtimeSensorData(@RequestBody MicroserviceRealtimeRequest request);

    /**
     * Get sensor data for a single hive (GET)
     * 
     * @param hiveId The ID of the hive to get sensor data for
     * @return Sensor data for the specified hive
     */
    @GetMapping("/api/sensor-data/hive/{hiveId}")
    MicroserviceSensorDataDTO getSensorDataForHive(@PathVariable("hiveId") UUID hiveId);
}

