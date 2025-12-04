package com.beehivemonitor.service;

import com.beehivemonitor.entity.SensorReading;
import com.beehivemonitor.repository.SensorReadingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Scheduled service for cleaning up old data to prevent database bloat.
 * Uses fixedDelay scheduling (different from cron expression) to run
 * cleanup tasks at regular intervals.
 */
@Service
public class DataCleanupScheduler {

    private static final Logger logger = LoggerFactory.getLogger(DataCleanupScheduler.class);

    @Autowired
    private SensorReadingRepository sensorReadingRepository;

    /**
     * Retention period in days for sensor readings.
     * Configurable via application.properties: data.retention.days
     * Default: 30 days
     */
    @Value("${data.retention.days:30}")
    private int retentionDays;

    /**
     * Cleanup interval in milliseconds.
     * Configurable via application.properties: data.cleanup.interval.ms
     * Default: 24 hours (86400000 ms)
     */
    @Value("${data.cleanup.interval.ms:86400000}")
    private long cleanupIntervalMs;

    /**
     * Cleans up old sensor readings that are older than the retention period.
     * 
     * Uses fixedDelay scheduling - runs with a fixed delay after the previous
     * execution completes. This ensures the cleanup completes before the next run starts.
     * 
     * fixedDelay = 86400000 ms = 24 hours
     * This means: wait 24 hours after the previous cleanup finishes before starting the next one
     * 
     * @Scheduled(fixedDelay = 86400000) means:
     * - Run the cleanup
     * - Wait for it to complete
     * - Wait 24 hours
     * - Run again
     */
    @Scheduled(fixedDelayString = "${data.cleanup.interval.ms:86400000}", initialDelay = 3600000)
    @Transactional
    public void cleanupOldSensorReadings() {
        logger.info("Starting scheduled cleanup of old sensor readings (retention: {} days)...", retentionDays);
        
        try {
            // Calculate cutoff date (readings older than this will be deleted)
            LocalDateTime cutoffDate = LocalDateTime.now().minusDays(retentionDays);
            logger.debug("Cutoff date for cleanup: {}", cutoffDate);

            // Count readings that will be deleted
            long countBeforeDeletion = sensorReadingRepository.countByTimestampBefore(cutoffDate);
            
            if (countBeforeDeletion > 0) {
                // Delete all sensor readings older than the cutoff date
                sensorReadingRepository.deleteByTimestampBefore(cutoffDate);
                
                logger.info("Cleanup completed. Deleted {} old sensor readings (older than {} days)", 
                    countBeforeDeletion, retentionDays);
            } else {
                logger.debug("Cleanup completed. No old sensor readings found to delete (retention: {} days)", 
                    retentionDays);
            }

        } catch (Exception e) {
            logger.error("Error during scheduled sensor readings cleanup: {}", e.getMessage(), e);
        }
    }
}

