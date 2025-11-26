package com.beehivemonitor.service;

import com.beehivemonitor.controller.SensorController;
import com.beehivemonitor.entity.Alert;
import com.beehivemonitor.entity.Hive;
import com.beehivemonitor.entity.User;
import com.beehivemonitor.repository.AlertRepository;
import com.beehivemonitor.repository.HiveRepository;
import com.beehivemonitor.repository.UserRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AlertService {

    @Autowired
    private AlertRepository alertRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private HiveRepository hiveRepository;

    @Autowired
    private SensorService sensorService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<Alert> getAllAlertsByUser(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
        List<Alert> alerts = alertRepository.findByUserId(user.getId());
        
        // Check and update trigger status for each alert
        Map<Long, SensorController.HiveSensorData> sensorData = sensorService.getRealtimeDataForAllHives(email);
        for (Alert alert : alerts) {
            boolean triggered = checkAlertTriggered(alert, sensorData.get(alert.getHive().getId()));
            alert.setIsTriggered(triggered);
        }
        
        return alerts;
    }

    public Alert getAlertById(Long id, String email) {
        Alert alert = alertRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Alert not found"));
        
        // Verify ownership through hive
        Hive hive = alert.getHive();
        if (hive == null || hive.getUser() == null || !hive.getUser().getEmail().equals(email)) {
            throw new RuntimeException("Unauthorized access to alert");
        }
        
        return alert;
    }

    @Transactional
    public Alert createAlert(Alert alert, String email) {
        Hive hive = hiveRepository.findById(alert.getHive().getId())
            .orElseThrow(() -> new RuntimeException("Hive not found"));
        
        // Verify ownership
        if (!hive.getUser().getEmail().equals(email)) {
            throw new RuntimeException("Unauthorized access to hive");
        }
        
        alert.setHive(hive);
        alert.setIsTriggered(false);
        
        // Check initial trigger status
        Map<Long, SensorController.HiveSensorData> sensorData = sensorService.getRealtimeDataForAllHives(email);
        alert.setIsTriggered(checkAlertTriggered(alert, sensorData.get(hive.getId())));
        
        return alertRepository.save(alert);
    }

    @Transactional
    public Alert updateAlert(Long id, Alert updatedAlert, String email) {
        Alert alert = getAlertById(id, email);
        alert.setName(updatedAlert.getName());
        alert.setHive(updatedAlert.getHive());
        alert.setTriggerConditions(updatedAlert.getTriggerConditions());
        
        // Re-check trigger status
        Map<Long, SensorController.HiveSensorData> sensorData = sensorService.getRealtimeDataForAllHives(email);
        alert.setIsTriggered(checkAlertTriggered(alert, sensorData.get(alert.getHive().getId())));
        
        return alertRepository.save(alert);
    }

    @Transactional
    public void deleteAlert(Long id, String email) {
        Alert alert = getAlertById(id, email);
        alertRepository.delete(alert);
    }

    @Transactional
    public Alert resetAlert(Long id, String email) {
        Alert alert = getAlertById(id, email);
        alert.setIsTriggered(false);
        return alertRepository.save(alert);
    }

    private boolean checkAlertTriggered(Alert alert, SensorController.HiveSensorData sensorData) {
        if (sensorData == null || alert.getTriggerConditions() == null || alert.getTriggerConditions().isEmpty()) {
            return false;
        }

        try {
            List<Map<String, Object>> conditions = objectMapper.readValue(
                alert.getTriggerConditions(),
                new TypeReference<List<Map<String, Object>>>() {}
            );

            // All conditions must be met (AND logic)
            for (Map<String, Object> condition : conditions) {
                String parameter = (String) condition.get("parameter");
                String operator = (String) condition.get("operator");
                Double threshold = ((Number) condition.get("value")).doubleValue();
                Double currentValue = getParameterValue(sensorData, parameter);

                if (currentValue == null) {
                    return false;
                }

                boolean conditionMet = false;
                switch (operator) {
                    case ">":
                        conditionMet = currentValue > threshold;
                        break;
                    case ">=":
                        conditionMet = currentValue >= threshold;
                        break;
                    case "<":
                        conditionMet = currentValue < threshold;
                        break;
                    case "<=":
                        conditionMet = currentValue <= threshold;
                        break;
                }

                if (!conditionMet) {
                    return false; // One condition failed, alert not triggered
                }
            }

            return true; // All conditions met
        } catch (Exception e) {
            return false;
        }
    }

    private Double getParameterValue(SensorController.HiveSensorData sensorData, String parameter) {
        switch (parameter.toLowerCase()) {
            case "temperature":
                return sensorData.temperature;
            case "humidity":
                return sensorData.humidity;
            case "co2":
                return sensorData.co2;
            case "sound":
            case "soundlevel":
                return sensorData.soundLevel;
            default:
                return null;
        }
    }
}

