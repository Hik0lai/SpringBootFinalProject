package com.beehivemonitor.sensor.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RealtimeDataResponse {
    private Map<Long, HiveSensorDataDTO> sensorData;
}

