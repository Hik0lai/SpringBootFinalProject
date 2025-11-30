package com.beehivemonitor.service;

import com.beehivemonitor.entity.User;
import com.beehivemonitor.entity.UserSettings;
import com.beehivemonitor.repository.UserRepository;
import com.beehivemonitor.repository.UserSettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SettingsService {

    @Autowired
    private UserSettingsRepository userSettingsRepository;

    @Autowired
    private UserRepository userRepository;

    public UserSettings getSettingsByEmail(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        return userSettingsRepository.findByUser(user)
            .orElseGet(() -> {
                // Create default settings if not exists
                UserSettings settings = new UserSettings();
                settings.setUser(user);
                settings.setMeasurementIntervalMinutes(1);
                return userSettingsRepository.save(settings);
            });
    }

    @Transactional
    public UserSettings updateMeasurementInterval(String email, Integer intervalMinutes) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        UserSettings settings = userSettingsRepository.findByUser(user)
            .orElseGet(() -> {
                UserSettings newSettings = new UserSettings();
                newSettings.setUser(user);
                return newSettings;
            });
        
        settings.setMeasurementIntervalMinutes(intervalMinutes);
        return userSettingsRepository.save(settings);
    }
}

