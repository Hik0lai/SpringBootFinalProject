package com.beehivemonitor.service;

import com.beehivemonitor.entity.User;
import com.beehivemonitor.entity.UserSettings;
import com.beehivemonitor.repository.UserRepository;
import com.beehivemonitor.repository.UserSettingsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit Test for UserSettingsService
 * Tests business logic in isolation using Mockito
 */
@ExtendWith(MockitoExtension.class)
class UserSettingsServiceTest {

    @Mock
    private UserSettingsRepository userSettingsRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserSettingsService userSettingsService;

    private User testUser;
    private UserSettings testSettings;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(UUID.randomUUID());
        testUser.setName("Test User");
        testUser.setEmail("test@example.com");
        testUser.setRole(User.Role.USER);

        testSettings = new UserSettings();
        testSettings.setId(UUID.randomUUID());
        testSettings.setUser(testUser);
        testSettings.setMeasurementIntervalMinutes(5);
    }

    @Test
    void testGetSettingsByEmail_SettingsExist_ReturnsSettings() {
        // Arrange
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(userSettingsRepository.findByUser(testUser)).thenReturn(Optional.of(testSettings));

        // Act
        UserSettings result = userSettingsService.getSettingsByEmail("test@example.com");

        // Assert
        assertNotNull(result);
        assertEquals(testSettings.getId(), result.getId());
        assertEquals(5, result.getMeasurementIntervalMinutes());
        verify(userRepository, times(1)).findByEmail("test@example.com");
        verify(userSettingsRepository, times(1)).findByUser(testUser);
    }

    @Test
    void testGetSettingsByEmail_SettingsNotExist_CreatesDefault() {
        // Arrange
        UserSettings defaultSettings = new UserSettings();
        defaultSettings.setUser(testUser);
        defaultSettings.setMeasurementIntervalMinutes(1);

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(userSettingsRepository.findByUser(testUser)).thenReturn(Optional.empty());
        when(userSettingsRepository.save(any(UserSettings.class))).thenReturn(defaultSettings);

        // Act
        UserSettings result = userSettingsService.getSettingsByEmail("test@example.com");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getMeasurementIntervalMinutes());
        verify(userSettingsRepository, times(1)).save(any(UserSettings.class));
    }

    @Test
    void testUpdateMeasurementInterval_SettingsExist_UpdatesInterval() {
        // Arrange
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(userSettingsRepository.findByUser(testUser)).thenReturn(Optional.of(testSettings));
        when(userSettingsRepository.save(any(UserSettings.class))).thenReturn(testSettings);

        // Act
        UserSettings result = userSettingsService.updateMeasurementInterval("test@example.com", 10);

        // Assert
        assertNotNull(result);
        assertEquals(10, testSettings.getMeasurementIntervalMinutes());
        verify(userSettingsRepository, times(1)).save(testSettings);
    }

    @Test
    void testUpdateMeasurementInterval_SettingsNotExist_CreatesNew() {
        // Arrange
        UserSettings newSettings = new UserSettings();
        newSettings.setUser(testUser);
        newSettings.setMeasurementIntervalMinutes(15);

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(userSettingsRepository.findByUser(testUser)).thenReturn(Optional.empty());
        when(userSettingsRepository.save(any(UserSettings.class))).thenReturn(newSettings);

        // Act
        UserSettings result = userSettingsService.updateMeasurementInterval("test@example.com", 15);

        // Assert
        assertNotNull(result);
        assertEquals(15, newSettings.getMeasurementIntervalMinutes());
        verify(userSettingsRepository, times(1)).save(any(UserSettings.class));
    }
}

