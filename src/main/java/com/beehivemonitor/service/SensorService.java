package com.beehivemonitor.service;

import com.beehivemonitor.controller.SensorController;
import com.beehivemonitor.dto.SensorReadingDTO;
import com.beehivemonitor.entity.Hive;
import com.beehivemonitor.entity.User;
import com.beehivemonitor.repository.HiveRepository;
import com.beehivemonitor.repository.SensorReadingRepository;
import com.beehivemonitor.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class SensorService {

    @Autowired
    private SensorReadingRepository sensorReadingRepository;

    @Autowired
    private HiveRepository hiveRepository;

    @Autowired
    private UserRepository userRepository;

    private final Random random = new Random();

    public List<SensorReadingDTO> getLatestReadingsByHiveId(Long hiveId, String email) {
        Hive hive = hiveRepository.findById(hiveId)
            .orElseThrow(() -> new RuntimeException("Hive not found"));
        
        // Verify ownership
        if (!hive.getUser().getEmail().equals(email)) {
            throw new RuntimeException("Unauthorized access to hive");
        }
        
        return sensorReadingRepository.findLatestReadingsByHiveId(hiveId).stream()
            .map(reading -> new SensorReadingDTO(reading.getType(), reading.getValue(), reading.getUnit()))
            .collect(Collectors.toList());
    }

    /**
     * Simulates microservice that returns real-time sensor data for all hives
     * Generates random values within specified ranges for each hive
     */
    public Map<Long, SensorController.HiveSensorData> getRealtimeDataForAllHives(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<Hive> hives = hiveRepository.findByUser(user);
        Map<Long, SensorController.HiveSensorData> sensorDataMap = new HashMap<>();
        
        for (Hive hive : hives) {
            // Generate random values within specified ranges
            // Temperature: 15-30°C
            double temperature = 15 + (30 - 15) * random.nextDouble();
            
            // Humidity: 5-60%
            double humidity = 5 + (60 - 5) * random.nextDouble();
            
            // CO2: 400-2000 ppm
            double co2 = 400 + (2000 - 400) * random.nextDouble();
            
            // Sound level: 40-100 dB
            double soundLevel = 40 + (100 - 40) * random.nextDouble();
            
            sensorDataMap.put(hive.getId(), new SensorController.HiveSensorData(
                Math.round(temperature * 10.0) / 10.0,  // Round to 1 decimal
                Math.round(humidity * 10.0) / 10.0,      // Round to 1 decimal
                Math.round(co2),                          // Round to integer
                Math.round(soundLevel * 10.0) / 10.0      // Round to 1 decimal
            ));
        }
        
        return sensorDataMap;
    }
}

