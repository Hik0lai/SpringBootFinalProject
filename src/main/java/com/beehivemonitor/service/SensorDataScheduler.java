package com.beehivemonitor.service;

import com.beehivemonitor.entity.Hive;
import com.beehivemonitor.entity.User;
import com.beehivemonitor.entity.UserSettings;
import com.beehivemonitor.repository.HiveRepository;
import com.beehivemonitor.repository.UserRepository;
import com.beehivemonitor.repository.UserSettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;

@Service
public class SensorDataScheduler {

    @Autowired
    private HiveRepository hiveRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserSettingsRepository userSettingsRepository;

    @Autowired
    private SensorService sensorService;

    private final Random random = new Random();

    // This will run every minute, but we'll check user settings
    @Scheduled(fixedRate = 60000) // Run every minute
    @Transactional
    public void saveSensorDataForAllUsers() {
        List<User> users = userRepository.findAll();
        
        for (User user : users) {
            UserSettings settings = userSettingsRepository.findByUser(user)
                .orElseGet(() -> {
                    UserSettings defaultSettings = new UserSettings();
                    defaultSettings.setUser(user);
                    defaultSettings.setMeasurementIntervalMinutes(1);
                    return userSettingsRepository.save(defaultSettings);
                });
            
            // Check if it's time to save data based on user's interval
            // For simplicity, we'll save for all users every minute
            // In a real system, you'd track last save time per user
            List<Hive> hives = hiveRepository.findByUser(user);
            
            for (Hive hive : hives) {
                // Generate and save sensor data
                double temperature = 15 + (30 - 15) * random.nextDouble();
                double humidity = 5 + (60 - 5) * random.nextDouble();
                double co2 = 400 + (2000 - 400) * random.nextDouble();
                double soundLevel = 40 + (100 - 40) * random.nextDouble();
                double weight = 4 + (12 - 4) * random.nextDouble();
                
                sensorService.saveHistoricalData(
                    hive,
                    Math.round(temperature * 10.0) / 10.0,
                    Math.round(humidity * 10.0) / 10.0,
                    Math.round(co2),
                    Math.round(soundLevel * 10.0) / 10.0,
                    Math.round(weight * 10.0) / 10.0
                );
            }
        }
    }
}

