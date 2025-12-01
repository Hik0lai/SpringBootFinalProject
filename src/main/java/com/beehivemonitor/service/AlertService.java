package com.beehivemonitor.service;

import com.beehivemonitor.client.NotificationMicroserviceClient;
import com.beehivemonitor.controller.SensorController;
import com.beehivemonitor.dto.NotificationRequest;
import com.beehivemonitor.entity.Alert;
import com.beehivemonitor.entity.Hive;
import com.beehivemonitor.entity.User;
import com.beehivemonitor.repository.AlertRepository;
import com.beehivemonitor.repository.HiveRepository;
import com.beehivemonitor.repository.UserRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Autowired
    private NotificationMicroserviceClient notificationMicroserviceClient;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<Alert> getAllAlertsByUser(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
        List<Alert> alerts = alertRepository.findByUserId(user.getId());
        
        // Check and update trigger status for each alert
        Map<Long, SensorController.HiveSensorData> sensorData = sensorService.getRealtimeDataForAllHives(email);
        for (Alert alert : alerts) {
            boolean previousTriggered = alert.getIsTriggered() != null ? alert.getIsTriggered() : false;
            boolean triggered = checkAlertTriggered(alert, sensorData.get(alert.getHive().getId()));
            alert.setIsTriggered(triggered);
            
            // Send notification if alert was just triggered (was false, now true) and user has email notifications enabled
            if (!previousTriggered && triggered && user.getEmailNotificationEnabled() != null && user.getEmailNotificationEnabled()) {
                sendEmailNotification(user, alert);
            }
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
        boolean triggered = checkAlertTriggered(alert, sensorData.get(hive.getId()));
        alert.setIsTriggered(triggered);
        
        Alert savedAlert = alertRepository.save(alert);
        
        // Send notification if alert is triggered and user has email notifications enabled
        User user = hive.getUser();
        if (triggered && user.getEmailNotificationEnabled() != null && user.getEmailNotificationEnabled()) {
            sendEmailNotification(user, savedAlert);
        }
        
        return savedAlert;
    }

    @Transactional
    public Alert updateAlert(Long id, Alert updatedAlert, String email) {
        Alert alert = getAlertById(id, email);
        boolean previousTriggered = alert.getIsTriggered() != null ? alert.getIsTriggered() : false;
        alert.setName(updatedAlert.getName());
        alert.setHive(updatedAlert.getHive());
        alert.setTriggerConditions(updatedAlert.getTriggerConditions());
        
        // Re-check trigger status
        Map<Long, SensorController.HiveSensorData> sensorData = sensorService.getRealtimeDataForAllHives(email);
        boolean triggered = checkAlertTriggered(alert, sensorData.get(alert.getHive().getId()));
        alert.setIsTriggered(triggered);
        
        Alert savedAlert = alertRepository.save(alert);
        
        // Send notification if alert was just triggered (was false, now true) and user has email notifications enabled
        User user = alert.getHive().getUser();
        if (!previousTriggered && triggered && user.getEmailNotificationEnabled() != null && user.getEmailNotificationEnabled()) {
            sendEmailNotification(user, savedAlert);
        }
        
        return savedAlert;
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
            case "int. temperature":
            case "int temperature":
                return sensorData.temperature;
            case "externaltemperature":
            case "ext. temperature":
            case "ext temperature":
                return sensorData.externalTemperature;
            case "humidity":
                return sensorData.humidity;
            case "co2":
                return sensorData.co2;
            case "sound":
            case "soundlevel":
                return sensorData.soundLevel;
            case "weight":
                return sensorData.weight;
            default:
                return null;
        }
    }

    /**
     * Sends email notification via notification microservice
     */
    private void sendEmailNotification(User user, Alert alert) {
        try {
            NotificationRequest request = new NotificationRequest(
                user.getEmail(),
                "Alert Triggered",
                "Alert triggered: " + alert.getName(),
                "EMAIL",
                alert.getId()
            );
            
            notificationMicroserviceClient.sendNotification(request);
        } catch (FeignException e) {
            // Log error but don't fail the alert check
            System.err.println("Failed to send email notification: " + e.getMessage());
        }
    }
}

