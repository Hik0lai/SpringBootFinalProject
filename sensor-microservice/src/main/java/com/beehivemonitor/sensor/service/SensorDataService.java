package com.beehivemonitor.sensor.service;

import com.beehivemonitor.sensor.dto.HiveSensorDataDTO;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
public class SensorDataService {

    private final Random random = new Random();

    /**
     * Generates random sensor data for a single hive
     */
    public HiveSensorDataDTO generateSensorData() {
        // Internal Temperature: 15-30°C
        double temperature = 15 + (30 - 15) * random.nextDouble();
        
        // External Temperature: 15-30°C
        double externalTemperature = 15 + (30 - 15) * random.nextDouble();
        
        // Humidity: 5-60%
        double humidity = 5 + (60 - 5) * random.nextDouble();
        
        // CO2: 400-2000 ppm
        double co2 = 400 + (2000 - 400) * random.nextDouble();
        
        // Sound level: 40-100 dB
        double soundLevel = 40 + (100 - 40) * random.nextDouble();
        
        // Weight: 4-12 kg
        double weight = 4 + (12 - 4) * random.nextDouble();
        
        // Round values
        double roundedTemp = Math.round(temperature * 10.0) / 10.0;
        double roundedExtTemp = Math.round(externalTemperature * 10.0) / 10.0;
        double roundedHumidity = Math.round(humidity * 10.0) / 10.0;
        double roundedCo2 = Math.round(co2);
        double roundedSound = Math.round(soundLevel * 10.0) / 10.0;
        double roundedWeight = Math.round(weight * 10.0) / 10.0;
        
        return new HiveSensorDataDTO(
            roundedTemp,
            roundedExtTemp,
            roundedHumidity,
            roundedCo2,
            roundedSound,
            roundedWeight
        );
    }

    /**
     * Generates sensor data for multiple hives
     */
    public Map<Long, HiveSensorDataDTO> generateSensorDataForHives(List<Long> hiveIds) {
        Map<Long, HiveSensorDataDTO> sensorDataMap = new HashMap<>();
        
        for (Long hiveId : hiveIds) {
            sensorDataMap.put(hiveId, generateSensorData());
        }
        
        return sensorDataMap;
    }
}


