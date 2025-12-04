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
import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit Test for AlertService
 * Tests business logic in isolation using Mockito
 */
@ExtendWith(MockitoExtension.class)
class AlertServiceTest {

    @Mock
    private AlertRepository alertRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private HiveRepository hiveRepository;

    @Mock
    private SensorService sensorService;

    @Mock
    private NotificationMicroserviceClient notificationMicroserviceClient;

    @InjectMocks
    private AlertService alertService;

    private User testUser;
    private Hive testHive;
    private Alert testAlert;
    private UUID userId;
    private UUID hiveId;
    private UUID alertId;
    private SensorController.HiveSensorData sensorData;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        hiveId = UUID.randomUUID();
        alertId = UUID.randomUUID();

        testUser = new User();
        testUser.setId(userId);
        testUser.setName("Test User");
        testUser.setEmail("test@example.com");
        testUser.setEmailNotificationEnabled(true);

        testHive = new Hive();
        testHive.setId(hiveId);
        testHive.setName("Test Hive");
        testHive.setUser(testUser);

        testAlert = new Alert();
        testAlert.setId(alertId);
        testAlert.setName("Test Alert");
        testAlert.setHive(testHive);
        testAlert.setTriggerConditions("[{\"parameter\":\"temperature\",\"operator\":\">\",\"value\":30}]");
        testAlert.setIsTriggered(false);

        sensorData = new SensorController.HiveSensorData(25.0, 20.0, 50.0, 400.0, 30.0, 15.0);
    }

    @Test
    void testGetAllAlertsByUser_Success() {
        // Arrange
        List<Alert> alerts = Arrays.asList(testAlert);
        Map<UUID, SensorController.HiveSensorData> sensorDataMap = new HashMap<>();
        sensorDataMap.put(hiveId, sensorData);

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(alertRepository.findByUserId(userId)).thenReturn(alerts);
        when(sensorService.getRealtimeDataForAllHives("test@example.com")).thenReturn(sensorDataMap);

        // Act
        List<Alert> result = alertService.getAllAlertsByUser("test@example.com");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(userRepository, times(1)).findByEmail("test@example.com");
        verify(alertRepository, times(1)).findByUserId(userId);
        verify(sensorService, times(1)).getRealtimeDataForAllHives("test@example.com");
    }

    @Test
    void testGetAllAlertsByUser_UserNotFound_ThrowsException() {
        // Arrange
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            alertService.getAllAlertsByUser("nonexistent@example.com");
        });

        assertEquals("User not found", exception.getMessage());
        verify(alertRepository, never()).findByUserId(any(UUID.class));
    }

    @Test
    void testGetAlertById_Success() {
        // Arrange
        when(alertRepository.findById(alertId)).thenReturn(Optional.of(testAlert));

        // Act
        Alert result = alertService.getAlertById(alertId, "test@example.com");

        // Assert
        assertNotNull(result);
        assertEquals(testAlert.getId(), result.getId());
        verify(alertRepository, times(1)).findById(alertId);
    }

    @Test
    void testGetAlertById_Unauthorized_ThrowsException() {
        // Arrange
        when(alertRepository.findById(alertId)).thenReturn(Optional.of(testAlert));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            alertService.getAlertById(alertId, "other@example.com");
        });

        assertEquals("Unauthorized access to alert", exception.getMessage());
    }

    @Test
    void testGetAlertById_AlertNotFound_ThrowsException() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();
        when(alertRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            alertService.getAlertById(nonExistentId, "test@example.com");
        });

        assertEquals("Alert not found", exception.getMessage());
    }

    @Test
    void testCreateAlert_Success() {
        // Arrange
        Alert newAlert = new Alert();
        newAlert.setName("New Alert");
        newAlert.setHive(testHive);
        newAlert.setTriggerConditions("[{\"parameter\":\"temperature\",\"operator\":\">\",\"value\":30}]");

        Map<UUID, SensorController.HiveSensorData> sensorDataMap = new HashMap<>();
        sensorDataMap.put(hiveId, sensorData);

        when(hiveRepository.findById(hiveId)).thenReturn(Optional.of(testHive));
        when(sensorService.getRealtimeDataForAllHives("test@example.com")).thenReturn(sensorDataMap);
        when(alertRepository.save(any(Alert.class))).thenReturn(newAlert);

        // Act
        Alert result = alertService.createAlert(newAlert, "test@example.com");

        // Assert
        assertNotNull(result);
        assertEquals(testHive, newAlert.getHive());
        verify(hiveRepository, times(1)).findById(hiveId);
        verify(alertRepository, times(1)).save(newAlert);
    }

    @Test
    void testCreateAlert_Unauthorized_ThrowsException() {
        // Arrange
        Alert newAlert = new Alert();
        newAlert.setHive(testHive);

        when(hiveRepository.findById(hiveId)).thenReturn(Optional.of(testHive));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            alertService.createAlert(newAlert, "other@example.com");
        });

        assertEquals("Unauthorized access to hive", exception.getMessage());
        verify(alertRepository, never()).save(any(Alert.class));
    }

    @Test
    void testUpdateAlert_Success() {
        // Arrange
        Alert updatedAlert = new Alert();
        updatedAlert.setName("Updated Alert");
        updatedAlert.setHive(testHive);
        updatedAlert.setTriggerConditions("[{\"parameter\":\"temperature\",\"operator\":\">\",\"value\":30}]");

        Map<UUID, SensorController.HiveSensorData> sensorDataMap = new HashMap<>();
        sensorDataMap.put(hiveId, sensorData);

        when(alertRepository.findById(alertId)).thenReturn(Optional.of(testAlert));
        when(sensorService.getRealtimeDataForAllHives("test@example.com")).thenReturn(sensorDataMap);
        when(alertRepository.save(any(Alert.class))).thenReturn(testAlert);

        // Act
        Alert result = alertService.updateAlert(alertId, updatedAlert, "test@example.com");

        // Assert
        assertNotNull(result);
        assertEquals("Updated Alert", testAlert.getName());
        verify(alertRepository, times(1)).save(testAlert);
    }

    @Test
    void testDeleteAlert_Success() {
        // Arrange
        when(alertRepository.findById(alertId)).thenReturn(Optional.of(testAlert));
        doNothing().when(alertRepository).delete(testAlert);

        // Act
        alertService.deleteAlert(alertId, "test@example.com");

        // Assert
        verify(alertRepository, times(1)).findById(alertId);
        verify(alertRepository, times(1)).delete(testAlert);
    }

    @Test
    void testResetAlert_Success() {
        // Arrange
        testAlert.setIsTriggered(true);
        when(alertRepository.findById(alertId)).thenReturn(Optional.of(testAlert));
        when(alertRepository.save(any(Alert.class))).thenReturn(testAlert);

        // Act
        Alert result = alertService.resetAlert(alertId, "test@example.com");

        // Assert
        assertNotNull(result);
        assertFalse(testAlert.getIsTriggered());
        verify(alertRepository, times(1)).save(testAlert);
    }
}

