package com.beehivemonitor.service;

import com.beehivemonitor.client.SensorMicroserviceClient;
import com.beehivemonitor.controller.SensorController;
import com.beehivemonitor.dto.MicroserviceRealtimeRequest;
import com.beehivemonitor.dto.MicroserviceRealtimeResponse;
import com.beehivemonitor.dto.MicroserviceSensorDataDTO;
import com.beehivemonitor.dto.SensorReadingDTO;
import com.beehivemonitor.entity.Hive;
import com.beehivemonitor.entity.HiveSensorData;
import com.beehivemonitor.entity.SensorReading;
import com.beehivemonitor.entity.User;
import com.beehivemonitor.entity.UserSettings;
import com.beehivemonitor.repository.HiveRepository;
import com.beehivemonitor.repository.HiveSensorDataRepository;
import com.beehivemonitor.repository.SensorReadingRepository;
import com.beehivemonitor.repository.UserRepository;
import com.beehivemonitor.repository.UserSettingsRepository;
import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit Test for SensorService
 * Tests sensor data retrieval, real-time data fetching, historical data, and fallback mechanisms
 */
@ExtendWith(MockitoExtension.class)
class SensorServiceTest {

    @Mock
    private SensorReadingRepository sensorReadingRepository;

    @Mock
    private HiveRepository hiveRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private HiveSensorDataRepository hiveSensorDataRepository;

    @Mock
    private UserSettingsRepository userSettingsRepository;

    @Mock
    private SensorMicroserviceClient sensorMicroserviceClient;

    @InjectMocks
    private SensorService sensorService;

    private User testUser;
    private Hive testHive;
    private UUID hiveId;
    private UUID userId;
    private String userEmail;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        hiveId = UUID.randomUUID();
        userEmail = "test@example.com";

        testUser = new User();
        testUser.setId(userId);
        testUser.setEmail(userEmail);
        testUser.setName("Test User");
        testUser.setRole(User.Role.USER);

        testHive = new Hive();
        testHive.setId(hiveId);
        testHive.setName("Test Hive");
        testHive.setUser(testUser);
    }

    @Test
    void testGetLatestReadingsByHiveId_Success() {
        // Arrange
        when(hiveRepository.findById(hiveId)).thenReturn(Optional.of(testHive));

        SensorReading reading1 = new SensorReading();
        reading1.setType("Temperature");
        reading1.setValue(25.5);
        reading1.setUnit("°C");

        SensorReading reading2 = new SensorReading();
        reading2.setType("Humidity");
        reading2.setValue(60.0);
        reading2.setUnit("%");

        List<SensorReading> readings = Arrays.asList(reading1, reading2);
        when(sensorReadingRepository.findLatestReadingsByHiveId(hiveId)).thenReturn(readings);

        // Act
        List<SensorReadingDTO> result = sensorService.getLatestReadingsByHiveId(hiveId, userEmail);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Temperature", result.get(0).getType());
        assertEquals(25.5, result.get(0).getValue());
        assertEquals("°C", result.get(0).getUnit());
        verify(hiveRepository).findById(hiveId);
        verify(sensorReadingRepository).findLatestReadingsByHiveId(hiveId);
    }

    @Test
    void testGetLatestReadingsByHiveId_HiveNotFound() {
        // Arrange
        when(hiveRepository.findById(hiveId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                sensorService.getLatestReadingsByHiveId(hiveId, userEmail));

        assertEquals("Hive not found", exception.getMessage());
        verify(hiveRepository).findById(hiveId);
        verify(sensorReadingRepository, never()).findLatestReadingsByHiveId(any());
    }

    @Test
    void testGetLatestReadingsByHiveId_UnauthorizedAccess() {
        // Arrange
        when(hiveRepository.findById(hiveId)).thenReturn(Optional.of(testHive));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                sensorService.getLatestReadingsByHiveId(hiveId, "other@example.com"));

        assertEquals("Unauthorized access to hive", exception.getMessage());
        verify(hiveRepository).findById(hiveId);
        verify(sensorReadingRepository, never()).findLatestReadingsByHiveId(any());
    }

    @Test
    void testGetRealtimeSensorDataForHive_SuccessWithMicroservice() {
        // Arrange
        when(hiveRepository.findById(hiveId)).thenReturn(Optional.of(testHive));

        MicroserviceSensorDataDTO microserviceData = new MicroserviceSensorDataDTO();
        microserviceData.setTemperature(25.5);
        microserviceData.setExternalTemperature(20.0);
        microserviceData.setHumidity(60.0);
        microserviceData.setCo2(800.0);
        microserviceData.setSoundLevel(50.0);
        microserviceData.setWeight(6.5);

        when(sensorMicroserviceClient.getSensorDataForHive(hiveId)).thenReturn(microserviceData);

        // Act
        SensorController.HiveSensorData result = sensorService.getRealtimeSensorDataForHive(hiveId, userEmail);

        // Assert
        assertNotNull(result);
        assertEquals(25.5, result.temperature);
        assertEquals(20.0, result.externalTemperature);
        assertEquals(60.0, result.humidity);
        assertEquals(800.0, result.co2);
        assertEquals(50.0, result.soundLevel);
        assertEquals(6.5, result.weight);

        verify(hiveRepository).findById(hiveId);
        verify(sensorMicroserviceClient).getSensorDataForHive(hiveId);
        verify(hiveSensorDataRepository).save(any(HiveSensorData.class));
    }

    @Test
    void testGetRealtimeSensorDataForHive_FallbackToLocalGeneration() {
        // Arrange
        when(hiveRepository.findById(hiveId)).thenReturn(Optional.of(testHive));
        when(sensorMicroserviceClient.getSensorDataForHive(hiveId)).thenThrow(mock(FeignException.class));

        // Act
        SensorController.HiveSensorData result = sensorService.getRealtimeSensorDataForHive(hiveId, userEmail);

        // Assert
        assertNotNull(result);
        // Verify values are within expected ranges
        assertTrue(result.temperature >= 15.0 && result.temperature <= 30.0);
        assertTrue(result.externalTemperature >= 15.0 && result.externalTemperature <= 30.0);
        assertTrue(result.humidity >= 5.0 && result.humidity <= 60.0);
        assertTrue(result.co2 >= 400.0 && result.co2 <= 2000.0);
        assertTrue(result.soundLevel >= 40.0 && result.soundLevel <= 100.0);
        assertTrue(result.weight >= 4.0 && result.weight <= 12.0);

        verify(hiveRepository).findById(hiveId);
        verify(sensorMicroserviceClient).getSensorDataForHive(hiveId);
        verify(hiveSensorDataRepository).save(any(HiveSensorData.class));
    }

    @Test
    void testGetRealtimeSensorDataForHive_NullMicroserviceData() {
        // Arrange
        when(hiveRepository.findById(hiveId)).thenReturn(Optional.of(testHive));
        when(sensorMicroserviceClient.getSensorDataForHive(hiveId)).thenReturn(null);

        // Act
        SensorController.HiveSensorData result = sensorService.getRealtimeSensorDataForHive(hiveId, userEmail);

        // Assert - Should fallback to local generation
        assertNotNull(result);
        verify(hiveRepository).findById(hiveId);
        verify(sensorMicroserviceClient).getSensorDataForHive(hiveId);
        verify(hiveSensorDataRepository).save(any(HiveSensorData.class));
    }

    @Test
    void testGetRealtimeSensorDataForHive_HiveNotFound() {
        // Arrange
        when(hiveRepository.findById(hiveId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                sensorService.getRealtimeSensorDataForHive(hiveId, userEmail));

        assertEquals("Hive not found", exception.getMessage());
        verify(hiveRepository).findById(hiveId);
        verify(sensorMicroserviceClient, never()).getSensorDataForHive(any());
    }

    @Test
    void testGetRealtimeDataForAllHives_SuccessWithMicroservice() {
        // Arrange
        Hive hive2 = new Hive();
        UUID hiveId2 = UUID.randomUUID();
        hive2.setId(hiveId2);
        hive2.setName("Test Hive 2");
        hive2.setUser(testUser);

        List<Hive> hives = Arrays.asList(testHive, hive2);
        when(hiveRepository.findAll()).thenReturn(hives);

        MicroserviceSensorDataDTO data1 = new MicroserviceSensorDataDTO();
        data1.setTemperature(25.5);
        data1.setExternalTemperature(20.0);
        data1.setHumidity(60.0);
        data1.setCo2(800.0);
        data1.setSoundLevel(50.0);
        data1.setWeight(6.5);

        MicroserviceSensorDataDTO data2 = new MicroserviceSensorDataDTO();
        data2.setTemperature(27.0);
        data2.setExternalTemperature(22.0);
        data2.setHumidity(55.0);
        data2.setCo2(900.0);
        data2.setSoundLevel(55.0);
        data2.setWeight(7.0);

        Map<UUID, MicroserviceSensorDataDTO> sensorDataMap = new HashMap<>();
        sensorDataMap.put(hiveId, data1);
        sensorDataMap.put(hiveId2, data2);

        MicroserviceRealtimeResponse response = new MicroserviceRealtimeResponse();
        response.setSensorData(sensorDataMap);

        when(sensorMicroserviceClient.getRealtimeSensorData(any(MicroserviceRealtimeRequest.class)))
                .thenReturn(response);

        // Act
        Map<UUID, SensorController.HiveSensorData> result = sensorService.getRealtimeDataForAllHives(userEmail);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.containsKey(hiveId));
        assertTrue(result.containsKey(hiveId2));
        assertEquals(25.5, result.get(hiveId).temperature);
        assertEquals(27.0, result.get(hiveId2).temperature);

        verify(hiveRepository).findAll();
        verify(sensorMicroserviceClient).getRealtimeSensorData(any(MicroserviceRealtimeRequest.class));
        verify(hiveSensorDataRepository, times(2)).save(any(HiveSensorData.class));
    }

    @Test
    void testGetRealtimeDataForAllHives_EmptyHives() {
        // Arrange
        when(hiveRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        Map<UUID, SensorController.HiveSensorData> result = sensorService.getRealtimeDataForAllHives(userEmail);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(hiveRepository).findAll();
        verify(sensorMicroserviceClient, never()).getRealtimeSensorData(any());
    }

    @Test
    void testGetRealtimeDataForAllHives_FallbackToLocalGeneration() {
        // Arrange
        when(hiveRepository.findAll()).thenReturn(Collections.singletonList(testHive));
        when(sensorMicroserviceClient.getRealtimeSensorData(any(MicroserviceRealtimeRequest.class)))
                .thenThrow(mock(FeignException.class));

        // Act
        Map<UUID, SensorController.HiveSensorData> result = sensorService.getRealtimeDataForAllHives(userEmail);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.containsKey(hiveId));
        
        SensorController.HiveSensorData data = result.get(hiveId);
        assertTrue(data.temperature >= 15.0 && data.temperature <= 30.0);
        
        verify(hiveRepository).findAll();
        verify(sensorMicroserviceClient).getRealtimeSensorData(any(MicroserviceRealtimeRequest.class));
        verify(hiveSensorDataRepository).save(any(HiveSensorData.class));
    }

    @Test
    void testGetRealtimeDataForAllHives_NullResponse() {
        // Arrange
        when(hiveRepository.findAll()).thenReturn(Collections.singletonList(testHive));
        when(sensorMicroserviceClient.getRealtimeSensorData(any(MicroserviceRealtimeRequest.class)))
                .thenReturn(null);

        // Act
        Map<UUID, SensorController.HiveSensorData> result = sensorService.getRealtimeDataForAllHives(userEmail);

        // Assert - Should fallback to local generation
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(hiveRepository).findAll();
        verify(sensorMicroserviceClient).getRealtimeSensorData(any(MicroserviceRealtimeRequest.class));
    }

    @Test
    void testGetRealtimeDataForAllHives_ResponseWithNullSensorData() {
        // Arrange
        when(hiveRepository.findAll()).thenReturn(Collections.singletonList(testHive));
        
        MicroserviceRealtimeResponse response = new MicroserviceRealtimeResponse();
        response.setSensorData(null);

        when(sensorMicroserviceClient.getRealtimeSensorData(any(MicroserviceRealtimeRequest.class)))
                .thenReturn(response);

        // Act
        Map<UUID, SensorController.HiveSensorData> result = sensorService.getRealtimeDataForAllHives(userEmail);

        // Assert - Should fallback to local generation
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(hiveRepository).findAll();
        verify(sensorMicroserviceClient).getRealtimeSensorData(any(MicroserviceRealtimeRequest.class));
    }

    @Test
    void testUpdateAllSensorData_Success() {
        // Arrange
        when(hiveRepository.findAll()).thenReturn(Collections.singletonList(testHive));
        
        MicroserviceSensorDataDTO microserviceData = new MicroserviceSensorDataDTO();
        microserviceData.setTemperature(25.5);
        microserviceData.setExternalTemperature(20.0);
        microserviceData.setHumidity(60.0);
        microserviceData.setCo2(800.0);
        microserviceData.setSoundLevel(50.0);
        microserviceData.setWeight(6.5);

        Map<UUID, MicroserviceSensorDataDTO> sensorDataMap = new HashMap<>();
        sensorDataMap.put(hiveId, microserviceData);

        MicroserviceRealtimeResponse response = new MicroserviceRealtimeResponse();
        response.setSensorData(sensorDataMap);

        when(sensorMicroserviceClient.getRealtimeSensorData(any(MicroserviceRealtimeRequest.class)))
                .thenReturn(response);

        // Act
        Map<UUID, SensorController.HiveSensorData> result = sensorService.updateAllSensorData(userEmail);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(hiveRepository).findAll();
    }

    @Test
    void testUpdateAllSensorData_ReturnsEmptyMapWhenNull() {
        // Arrange
        when(hiveRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        Map<UUID, SensorController.HiveSensorData> result = sensorService.updateAllSensorData(userEmail);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testSaveHistoricalData_Success() {
        // Act
        sensorService.saveHistoricalData(testHive, 25.5, 20.0, 60.0, 800.0, 50.0, 6.5);

        // Assert
        verify(hiveSensorDataRepository).save(argThat(sensorData -> {
            HiveSensorData data = (HiveSensorData) sensorData;
            return data.getHive().equals(testHive) &&
                   data.getTemperature() == 25.5 &&
                   data.getExternalTemperature() == 20.0 &&
                   data.getHumidity() == 60.0 &&
                   data.getCo2() == 800.0 &&
                   data.getSoundLevel() == 50.0 &&
                   data.getWeight() == 6.5 &&
                   data.getTimestamp() != null;
        }));
    }

    @Test
    void testGetHistoricalData_Success() {
        // Arrange
        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        LocalDateTime endDate = LocalDateTime.now();

        when(hiveRepository.findById(hiveId)).thenReturn(Optional.of(testHive));

        HiveSensorData historicalData1 = new HiveSensorData();
        historicalData1.setTemperature(25.0);
        historicalData1.setTimestamp(startDate.plusDays(1));

        HiveSensorData historicalData2 = new HiveSensorData();
        historicalData2.setTemperature(26.0);
        historicalData2.setTimestamp(startDate.plusDays(2));

        List<HiveSensorData> historicalData = Arrays.asList(historicalData1, historicalData2);
        when(hiveSensorDataRepository.findByHiveIdAndTimestampBetween(hiveId, startDate, endDate))
                .thenReturn(historicalData);

        // Act
        List<HiveSensorData> result = sensorService.getHistoricalData(hiveId, startDate, endDate, userEmail);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(hiveRepository).findById(hiveId);
        verify(hiveSensorDataRepository).findByHiveIdAndTimestampBetween(hiveId, startDate, endDate);
    }

    @Test
    void testGetHistoricalData_HiveNotFound() {
        // Arrange
        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        LocalDateTime endDate = LocalDateTime.now();

        when(hiveRepository.findById(hiveId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                sensorService.getHistoricalData(hiveId, startDate, endDate, userEmail));

        assertEquals("Hive not found", exception.getMessage());
        verify(hiveRepository).findById(hiveId);
        verify(hiveSensorDataRepository, never()).findByHiveIdAndTimestampBetween(any(), any(), any());
    }

    @Test
    void testGetHistoricalData_UnauthorizedAccess() {
        // Arrange
        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        LocalDateTime endDate = LocalDateTime.now();

        when(hiveRepository.findById(hiveId)).thenReturn(Optional.of(testHive));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                sensorService.getHistoricalData(hiveId, startDate, endDate, "other@example.com"));

        assertEquals("Unauthorized access to hive", exception.getMessage());
        verify(hiveRepository).findById(hiveId);
        verify(hiveSensorDataRepository, never()).findByHiveIdAndTimestampBetween(any(), any(), any());
    }

    @Test
    void testGetRealtimeSensorDataForHive_AllUsersCanView() {
        // Arrange - Test that all users can view sensor data (no ownership check)
        when(hiveRepository.findById(hiveId)).thenReturn(Optional.of(testHive));

        MicroserviceSensorDataDTO microserviceData = new MicroserviceSensorDataDTO();
        microserviceData.setTemperature(25.5);
        microserviceData.setExternalTemperature(20.0);
        microserviceData.setHumidity(60.0);
        microserviceData.setCo2(800.0);
        microserviceData.setSoundLevel(50.0);
        microserviceData.setWeight(6.5);

        when(sensorMicroserviceClient.getSensorDataForHive(hiveId)).thenReturn(microserviceData);

        // Act - Different user email
        SensorController.HiveSensorData result = sensorService.getRealtimeSensorDataForHive(hiveId, "other@example.com");

        // Assert - Should succeed (all users can view)
        assertNotNull(result);
        assertEquals(25.5, result.temperature);
        verify(hiveRepository).findById(hiveId);
    }

    @Test
    void testGetRealtimeDataForAllHives_AllUsersCanView() {
        // Arrange - Test that all users can view all hives
        Hive otherUserHive = new Hive();
        UUID otherHiveId = UUID.randomUUID();
        User otherUser = new User();
        otherUser.setEmail("other@example.com");
        
        otherUserHive.setId(otherHiveId);
        otherUserHive.setUser(otherUser);

        List<Hive> allHives = Arrays.asList(testHive, otherUserHive);
        when(hiveRepository.findAll()).thenReturn(allHives);

        when(sensorMicroserviceClient.getRealtimeSensorData(any(MicroserviceRealtimeRequest.class)))
                .thenThrow(mock(FeignException.class));

        // Act
        Map<UUID, SensorController.HiveSensorData> result = sensorService.getRealtimeDataForAllHives(userEmail);

        // Assert - Should return data for all hives, not just user's hives
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.containsKey(hiveId));
        assertTrue(result.containsKey(otherHiveId));
    }

    @Test
    void testSaveHistoricalData_ValuesAreRounded() {
        // Act
        sensorService.saveHistoricalData(testHive, 25.555, 20.123, 60.789, 800.456, 50.321, 6.987);

        // Assert - Values should be saved as-is (no rounding in saveHistoricalData, rounding happens before)
        verify(hiveSensorDataRepository).save(argThat(sensorData -> {
            HiveSensorData data = (HiveSensorData) sensorData;
            return data.getTemperature() == 25.555 &&
                   data.getExternalTemperature() == 20.123 &&
                   data.getHumidity() == 60.789 &&
                   data.getCo2() == 800.456 &&
                   data.getSoundLevel() == 50.321 &&
                   data.getWeight() == 6.987;
        }));
    }

    @Test
    void testLocalGeneration_ValuesAreWithinRanges() {
        // Arrange
        when(hiveRepository.findById(hiveId)).thenReturn(Optional.of(testHive));
        when(sensorMicroserviceClient.getSensorDataForHive(hiveId)).thenThrow(mock(FeignException.class));

        // Act - Call multiple times to test randomness
        Set<Double> temperatures = new HashSet<>();
        for (int i = 0; i < 10; i++) {
            SensorController.HiveSensorData result = sensorService.getRealtimeSensorDataForHive(hiveId, userEmail);
            temperatures.add(result.temperature);
            
            // Verify all values are within expected ranges
            assertTrue(result.temperature >= 15.0 && result.temperature <= 30.0, 
                    "Temperature should be between 15 and 30");
            assertTrue(result.externalTemperature >= 15.0 && result.externalTemperature <= 30.0,
                    "External temperature should be between 15 and 30");
            assertTrue(result.humidity >= 5.0 && result.humidity <= 60.0,
                    "Humidity should be between 5 and 60");
            assertTrue(result.co2 >= 400.0 && result.co2 <= 2000.0,
                    "CO2 should be between 400 and 2000");
            assertTrue(result.soundLevel >= 40.0 && result.soundLevel <= 100.0,
                    "Sound level should be between 40 and 100");
            assertTrue(result.weight >= 4.0 && result.weight <= 12.0,
                    "Weight should be between 4 and 12");
        }
        
        // Verify we get some variation (not all same values)
        assertTrue(temperatures.size() > 1, "Should generate different values");
    }
}

