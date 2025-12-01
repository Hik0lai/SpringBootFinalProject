package com.beehivemonitor.service;

import com.beehivemonitor.client.SensorMicroserviceClient;
import com.beehivemonitor.controller.SensorController;
import com.beehivemonitor.dto.MicroserviceRealtimeRequest;
import com.beehivemonitor.dto.MicroserviceRealtimeResponse;
import com.beehivemonitor.dto.MicroserviceSensorDataDTO;
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
import feign.FeignException;
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

    @Autowired
    private SensorMicroserviceClient sensorMicroserviceClient;

    private final Random random = new Random(); // Fallback if microservice is unavailable
    
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
     * Calls the sensor microservice to get real-time sensor data for all hives
     * Falls back to local generation if microservice is unavailable
     */
    public Map<Long, SensorController.HiveSensorData> getRealtimeDataForAllHives(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<Hive> hives = hiveRepository.findByUser(user);
        List<Long> hiveIds = hives.stream().map(Hive::getId).collect(Collectors.toList());
        
        if (hiveIds.isEmpty()) {
            return new HashMap<>();
        }
        
        // Try to call microservice
        try {
            MicroserviceRealtimeRequest request = new MicroserviceRealtimeRequest(hiveIds);
            MicroserviceRealtimeResponse response = sensorMicroserviceClient.getRealtimeSensorData(request);
            
            if (response != null && response.getSensorData() != null) {
                Map<Long, SensorController.HiveSensorData> sensorDataMap = new HashMap<>();
                
                for (Map.Entry<Long, MicroserviceSensorDataDTO> entry : response.getSensorData().entrySet()) {
                    Long hiveId = entry.getKey();
                    MicroserviceSensorDataDTO microserviceData = entry.getValue();
                    
                    // Convert microservice DTO to controller DTO
                    SensorController.HiveSensorData hiveSensorData = new SensorController.HiveSensorData(
                        microserviceData.getTemperature(),
                        microserviceData.getExternalTemperature(),
                        microserviceData.getHumidity(),
                        microserviceData.getCo2(),
                        microserviceData.getSoundLevel(),
                        microserviceData.getWeight()
                    );
                    
                    sensorDataMap.put(hiveId, hiveSensorData);
                    
                    // Save historical data
                    Hive hive = hives.stream().filter(h -> h.getId().equals(hiveId)).findFirst().orElse(null);
                    if (hive != null) {
                        saveHistoricalData(hive, 
                            microserviceData.getTemperature(),
                            microserviceData.getExternalTemperature(),
                            microserviceData.getHumidity(),
                            microserviceData.getCo2(),
                            microserviceData.getSoundLevel(),
                            microserviceData.getWeight()
                        );
                    }
                }
                
                return sensorDataMap;
            }
        } catch (FeignException e) {
            // Microservice unavailable - fallback to local generation
            System.err.println("Warning: Sensor microservice unavailable, using fallback: " + e.getMessage());
        }
        
        // Fallback: Generate locally if microservice is unavailable
        return generateSensorDataLocally(hives);
    }
    
    /**
     * Fallback method to generate sensor data locally if microservice is unavailable
     */
    private Map<Long, SensorController.HiveSensorData> generateSensorDataLocally(List<Hive> hives) {
        Map<Long, SensorController.HiveSensorData> sensorDataMap = new HashMap<>();
        
        for (Hive hive : hives) {
            // Generate random values within specified ranges
            double temperature = 15 + (30 - 15) * random.nextDouble();
            double externalTemperature = 15 + (30 - 15) * random.nextDouble();
            double humidity = 5 + (60 - 5) * random.nextDouble();
            double co2 = 400 + (2000 - 400) * random.nextDouble();
            double soundLevel = 40 + (100 - 40) * random.nextDouble();
            double weight = 4 + (12 - 4) * random.nextDouble();
            
            double roundedTemp = Math.round(temperature * 10.0) / 10.0;
            double roundedExtTemp = Math.round(externalTemperature * 10.0) / 10.0;
            double roundedHumidity = Math.round(humidity * 10.0) / 10.0;
            double roundedCo2 = Math.round(co2);
            double roundedSound = Math.round(soundLevel * 10.0) / 10.0;
            double roundedWeight = Math.round(weight * 10.0) / 10.0;
            
            sensorDataMap.put(hive.getId(), new SensorController.HiveSensorData(
                roundedTemp,
                roundedExtTemp,
                roundedHumidity,
                roundedCo2,
                roundedSound,
                roundedWeight
            ));
            
            // Save historical data
            saveHistoricalData(hive, roundedTemp, roundedExtTemp, roundedHumidity, roundedCo2, roundedSound, roundedWeight);
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
                List<Long> hiveIds = hives.stream().map(Hive::getId).collect(Collectors.toList());
                
                if (!hiveIds.isEmpty()) {
                    // Try to call microservice for scheduled data
                    try {
                        MicroserviceRealtimeRequest request = new MicroserviceRealtimeRequest(hiveIds);
                        MicroserviceRealtimeResponse response = sensorMicroserviceClient.getRealtimeSensorData(request);
                        
                        if (response != null && response.getSensorData() != null) {
                            for (Map.Entry<Long, MicroserviceSensorDataDTO> entry : response.getSensorData().entrySet()) {
                                Long hiveId = entry.getKey();
                                MicroserviceSensorDataDTO microserviceData = entry.getValue();
                                
                                Hive hive = hives.stream().filter(h -> h.getId().equals(hiveId)).findFirst().orElse(null);
                                if (hive != null) {
                                    saveHistoricalData(hive,
                                        microserviceData.getTemperature(),
                                        microserviceData.getExternalTemperature(),
                                        microserviceData.getHumidity(),
                                        microserviceData.getCo2(),
                                        microserviceData.getSoundLevel(),
                                        microserviceData.getWeight()
                                    );
                                }
                            }
                        } else {
                            // Fallback to local generation
                            generateAndSaveSensorDataLocally(hives);
                        }
                    } catch (FeignException e) {
                        // Microservice unavailable - fallback to local generation
                        System.err.println("Warning: Sensor microservice unavailable for scheduled save, using fallback: " + e.getMessage());
                        generateAndSaveSensorDataLocally(hives);
                    }
                }
                
                lastSaveTime.put(user.getId(), now);
            }
        }
    }
    
    /**
     * Fallback method to generate and save sensor data locally
     */
    private void generateAndSaveSensorDataLocally(List<Hive> hives) {
        for (Hive hive : hives) {
            double temperature = 15 + (30 - 15) * random.nextDouble();
            double externalTemperature = 15 + (30 - 15) * random.nextDouble();
            double humidity = 5 + (60 - 5) * random.nextDouble();
            double co2 = 400 + (2000 - 400) * random.nextDouble();
            double soundLevel = 40 + (100 - 40) * random.nextDouble();
            double weight = 4 + (12 - 4) * random.nextDouble();
            
            double roundedTemp = Math.round(temperature * 10.0) / 10.0;
            double roundedExtTemp = Math.round(externalTemperature * 10.0) / 10.0;
            double roundedHumidity = Math.round(humidity * 10.0) / 10.0;
            double roundedCo2 = Math.round(co2);
            double roundedSound = Math.round(soundLevel * 10.0) / 10.0;
            double roundedWeight = Math.round(weight * 10.0) / 10.0;
            
            saveHistoricalData(hive, roundedTemp, roundedExtTemp, roundedHumidity, roundedCo2, roundedSound, roundedWeight);
        }
    }
    
    @Transactional
    public void saveHistoricalData(Hive hive, double temperature, double externalTemperature, double humidity,
                                   double co2, double soundLevel, double weight) {
        HiveSensorData sensorData = new HiveSensorData();
        sensorData.setHive(hive);
        sensorData.setTemperature(temperature);
        sensorData.setExternalTemperature(externalTemperature);
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

