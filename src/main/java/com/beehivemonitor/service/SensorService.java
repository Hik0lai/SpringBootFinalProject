package com.beehivemonitor.service;

import com.beehivemonitor.controller.SensorController;
import com.beehivemonitor.dto.SensorReadingDTO;
import com.beehivemonitor.entity.Hive;
import com.beehivemonitor.entity.HiveSensorData;
import com.beehivemonitor.entity.User;
import com.beehivemonitor.entity.UserSettings;
import com.beehivemonitor.repository.HiveRepository;
import com.beehivemonitor.repository.HiveSensorDataRepository;
import com.beehivemonitor.repository.SensorReadingRepository;
import com.beehivemonitor.repository.UserRepository;
import com.beehivemonitor.repository.UserSettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@EnableScheduling
public class SensorService {

    @Autowired
    private SensorReadingRepository sensorReadingRepository;

    @Autowired
    private HiveRepository hiveRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private HiveSensorDataRepository hiveSensorDataRepository;

    @Autowired
    private UserSettingsRepository userSettingsRepository;

    private final Random random = new Random();
    
    // Track last save time per user
    private final Map<Long, LocalDateTime> lastSaveTime = new HashMap<>();

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
            
            // Weight: 4-12 kg
            double weight = 4 + (12 - 4) * random.nextDouble();
            
            double roundedTemp = Math.round(temperature * 10.0) / 10.0;
            double roundedHumidity = Math.round(humidity * 10.0) / 10.0;
            double roundedCo2 = Math.round(co2);
            double roundedSound = Math.round(soundLevel * 10.0) / 10.0;
            double roundedWeight = Math.round(weight * 10.0) / 10.0;
            
            sensorDataMap.put(hive.getId(), new SensorController.HiveSensorData(
                roundedTemp,
                roundedHumidity,
                roundedCo2,
                roundedSound,
                roundedWeight
            ));
        }
        
        return sensorDataMap;
    }
    
    /**
     * Scheduled task that saves sensor data at configured intervals for each user
     * Runs every minute and checks if enough time has passed based on user settings
     */
    @Scheduled(fixedRate = 60000) // Run every minute
    @Transactional
    public void saveSensorDataScheduled() {
        List<User> allUsers = userRepository.findAll();
        
        for (User user : allUsers) {
            UserSettings settings = userSettingsRepository.findByUser(user)
                .orElseGet(() -> {
                    UserSettings defaultSettings = new UserSettings();
                    defaultSettings.setUser(user);
                    defaultSettings.setMeasurementIntervalMinutes(1);
                    return userSettingsRepository.save(defaultSettings);
                });
            
            int intervalMinutes = settings.getMeasurementIntervalMinutes();
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime lastSave = lastSaveTime.get(user.getId());
            
            // Check if enough time has passed since last save
            if (lastSave == null || lastSave.plusMinutes(intervalMinutes).isBefore(now) || 
                lastSave.plusMinutes(intervalMinutes).isEqual(now)) {
                
                List<Hive> hives = hiveRepository.findByUser(user);
                
                for (Hive hive : hives) {
                    // Generate random values
                    double temperature = 15 + (30 - 15) * random.nextDouble();
                    double humidity = 5 + (60 - 5) * random.nextDouble();
                    double co2 = 400 + (2000 - 400) * random.nextDouble();
                    double soundLevel = 40 + (100 - 40) * random.nextDouble();
                    double weight = 4 + (12 - 4) * random.nextDouble();
                    
                    double roundedTemp = Math.round(temperature * 10.0) / 10.0;
                    double roundedHumidity = Math.round(humidity * 10.0) / 10.0;
                    double roundedCo2 = Math.round(co2);
                    double roundedSound = Math.round(soundLevel * 10.0) / 10.0;
                    double roundedWeight = Math.round(weight * 10.0) / 10.0;
                    
                    // Save historical data (no deletion - all data is kept)
                    saveHistoricalData(hive, roundedTemp, roundedHumidity, roundedCo2, roundedSound, roundedWeight);
                }
                
                lastSaveTime.put(user.getId(), now);
            }
        }
    }
    
    @Transactional
    public void saveHistoricalData(Hive hive, double temperature, double humidity, 
                                   double co2, double soundLevel, double weight) {
        HiveSensorData sensorData = new HiveSensorData();
        sensorData.setHive(hive);
        sensorData.setTemperature(temperature);
        sensorData.setHumidity(humidity);
        sensorData.setCo2(co2);
        sensorData.setSoundLevel(soundLevel);
        sensorData.setWeight(weight);
        sensorData.setTimestamp(LocalDateTime.now());
        hiveSensorDataRepository.save(sensorData);
    }
    
    public List<HiveSensorData> getHistoricalData(Long hiveId, LocalDateTime startDate, LocalDateTime endDate, String email) {
        Hive hive = hiveRepository.findById(hiveId)
            .orElseThrow(() -> new RuntimeException("Hive not found"));
        
        // Verify ownership
        if (!hive.getUser().getEmail().equals(email)) {
            throw new RuntimeException("Unauthorized access to hive");
        }
        
        return hiveSensorDataRepository.findByHiveIdAndTimestampBetween(hiveId, startDate, endDate);
    }
}

