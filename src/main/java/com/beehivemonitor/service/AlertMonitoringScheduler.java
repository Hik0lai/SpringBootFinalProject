package com.beehivemonitor.service;

import com.beehivemonitor.controller.SensorController;
import com.beehivemonitor.entity.Alert;
import com.beehivemonitor.entity.User;
import com.beehivemonitor.repository.AlertRepository;
import com.beehivemonitor.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Scheduled service for monitoring alerts and triggering notifications.
 * Uses cron expressions to periodically check all alerts and send notifications
 * when alert conditions are met.
 */
@Service
public class AlertMonitoringScheduler {

    private static final Logger logger = LoggerFactory.getLogger(AlertMonitoringScheduler.class);

    @Autowired
    private AlertRepository alertRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AlertService alertService;

    @Autowired
    private SensorService sensorService;

    /**
     * Monitors all alerts and checks if they should be triggered.
     * Runs every 5 minutes using cron expression.
     * 
     * Cron format: second minute hour day month weekday
     * Default expression means: at second 0, every 5 minutes, every hour, every day
     * 
     * Can be configured via application.properties: alert.monitoring.cron
     * Default: every 5 minutes
     */
    @Scheduled(cron = "${alert.monitoring.cron:0 */5 * * * ?}")
    @Transactional
    public void monitorAlerts() {
        logger.info("Starting scheduled alert monitoring check...");
        
        try {
            // Get all alerts from the database
            List<Alert> allAlerts = alertRepository.findAll();
            logger.debug("Found {} alerts to check", allAlerts.size());

            int triggeredCount = 0;
            int notificationSentCount = 0;

            for (Alert alert : allAlerts) {
                try {
                    // Get the user who owns this alert through the hive
                    User user = alert.getHive().getUser();
                    if (user == null) {
                        logger.warn("Alert {} has no associated user, skipping", alert.getId());
                        continue;
                    }

                    // Get current sensor data for all hives of this user
                    Map<UUID, SensorController.HiveSensorData> sensorDataMap = 
                        sensorService.getRealtimeDataForAllHives(user.getEmail());

                    // Get sensor data for this alert's hive
                    SensorController.HiveSensorData sensorData = 
                        sensorDataMap.get(alert.getHive().getId());

                    if (sensorData == null) {
                        logger.debug("No sensor data available for hive {}, skipping alert {}", 
                            alert.getHive().getId(), alert.getId());
                        continue;
                    }

                    // Check if alert conditions are met
                    boolean previousTriggered = alert.getIsTriggered() != null ? alert.getIsTriggered() : false;
                    boolean currentlyTriggered = alertService.checkAlertTriggered(alert, sensorData);

                    // Update alert trigger status if changed
                    if (previousTriggered != currentlyTriggered) {
                        alert.setIsTriggered(currentlyTriggered);
                        alertRepository.save(alert);
                        logger.info("Alert '{}' (ID: {}) status changed: {} -> {}", 
                            alert.getName(), alert.getId(), previousTriggered, currentlyTriggered);

                        // If alert was just triggered (went from false to true) and user has email notifications enabled
                        if (!previousTriggered && currentlyTriggered) {
                            triggeredCount++;
                            
                            if (user.getEmailNotificationEnabled() != null && user.getEmailNotificationEnabled()) {
                                try {
                                    alertService.sendEmailNotification(user, alert);
                                    notificationSentCount++;
                                    logger.info("Email notification sent for alert '{}' to user {}", 
                                        alert.getName(), user.getEmail());
                                } catch (Exception e) {
                                    logger.error("Failed to send email notification for alert {} to user {}: {}", 
                                        alert.getId(), user.getEmail(), e.getMessage());
                                }
                            } else {
                                logger.debug("Email notifications disabled for user {}, skipping notification", 
                                    user.getEmail());
                            }
                        }
                    }
                } catch (Exception e) {
                    logger.error("Error processing alert {}: {}", alert.getId(), e.getMessage(), e);
                    // Continue processing other alerts even if one fails
                }
            }

            logger.info("Alert monitoring completed. Triggered: {}, Notifications sent: {}, Total checked: {}", 
                triggeredCount, notificationSentCount, allAlerts.size());

        } catch (Exception e) {
            logger.error("Error in scheduled alert monitoring: {}", e.getMessage(), e);
        }
    }
}

