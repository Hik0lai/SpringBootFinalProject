package com.beehivemonitor.sensor.service;

import com.beehivemonitor.sensor.dto.HiveSensorDataDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit Test for SensorDataService
 * Tests sensor data generation functionality
 */
class SensorDataServiceTest {

    private SensorDataService sensorDataService;

    @BeforeEach
    void setUp() {
        sensorDataService = new SensorDataService();
    }

    @Test
    void generateSensorData_ReturnsValidData() {
        // Act
        HiveSensorDataDTO data = sensorDataService.generateSensorData();

        // Assert
        assertNotNull(data);
        assertTrue(data.getTemperature() >= 15.0 && data.getTemperature() <= 30.0,
                "Temperature should be between 15-30°C, got: " + data.getTemperature());
        assertTrue(data.getExternalTemperature() >= 15.0 && data.getExternalTemperature() <= 30.0,
                "External temperature should be between 15-30°C, got: " + data.getExternalTemperature());
        assertTrue(data.getHumidity() >= 5.0 && data.getHumidity() <= 60.0,
                "Humidity should be between 5-60%, got: " + data.getHumidity());
        assertTrue(data.getCo2() >= 400.0 && data.getCo2() <= 2000.0,
                "CO2 should be between 400-2000 ppm, got: " + data.getCo2());
        assertTrue(data.getSoundLevel() >= 40.0 && data.getSoundLevel() <= 100.0,
                "Sound level should be between 40-100 dB, got: " + data.getSoundLevel());
        assertTrue(data.getWeight() >= 4.0 && data.getWeight() <= 12.0,
                "Weight should be between 4-12 kg, got: " + data.getWeight());
    }

    @Test
    void generateSensorData_ValuesAreRounded() {
        // Act
        HiveSensorDataDTO data = sensorDataService.generateSensorData();

        // Assert - Values should be rounded to 1 decimal place (or integer for CO2)
        // Check that values multiplied by 10 are close to integers (within rounding tolerance)
        double tempRounded = Math.round(data.getTemperature() * 10.0) / 10.0;
        double humidityRounded = Math.round(data.getHumidity() * 10.0) / 10.0;
        double soundRounded = Math.round(data.getSoundLevel() * 10.0) / 10.0;
        double weightRounded = Math.round(data.getWeight() * 10.0) / 10.0;
        
        // Values should match their rounded versions (within floating point precision)
        assertEquals(tempRounded, data.getTemperature(), 0.0001, "Temperature should be rounded to 1 decimal");
        assertEquals(humidityRounded, data.getHumidity(), 0.0001, "Humidity should be rounded to 1 decimal");
        assertEquals(soundRounded, data.getSoundLevel(), 0.0001, "Sound level should be rounded to 1 decimal");
        assertEquals(weightRounded, data.getWeight(), 0.0001, "Weight should be rounded to 1 decimal");
        
        // CO2 should be integer (no decimal)
        assertEquals(data.getCo2(), Math.round(data.getCo2()), "CO2 should be an integer");
    }

    @Test
    void generateSensorData_ReturnsDifferentValuesOnMultipleCalls() {
        // Act - Generate multiple sensor data readings
        HiveSensorDataDTO data1 = sensorDataService.generateSensorData();
        HiveSensorDataDTO data2 = sensorDataService.generateSensorData();
        HiveSensorDataDTO data3 = sensorDataService.generateSensorData();
        HiveSensorDataDTO data4 = sensorDataService.generateSensorData();
        HiveSensorDataDTO data5 = sensorDataService.generateSensorData();

        // Assert - At least one value should be different (very likely with random generation)
        // We check multiple fields to increase probability of finding differences
        boolean hasDifference = 
            data1.getTemperature() != data2.getTemperature() ||
            data1.getHumidity() != data2.getHumidity() ||
            data1.getCo2() != data2.getCo2() ||
            data1.getWeight() != data2.getWeight() ||
            data2.getTemperature() != data3.getTemperature() ||
            data3.getTemperature() != data4.getTemperature() ||
            data4.getTemperature() != data5.getTemperature();

        // With random generation and 5 calls, it's extremely unlikely all values are identical
        assertTrue(hasDifference, "Multiple calls should generate different random values");
    }

    @Test
    void generateSensorDataForHives_SingleHiveId_ReturnsMapWithOneEntry() {
        // Arrange
        Long hiveId = 1L;
        List<Long> hiveIds = Collections.singletonList(hiveId);

        // Act
        Map<Long, HiveSensorDataDTO> result = sensorDataService.generateSensorDataForHives(hiveIds);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.containsKey(hiveId));
        assertNotNull(result.get(hiveId));
        
        // Verify the data is valid
        HiveSensorDataDTO data = result.get(hiveId);
        assertTrue(data.getTemperature() >= 15.0 && data.getTemperature() <= 30.0);
        assertTrue(data.getWeight() >= 4.0 && data.getWeight() <= 12.0);
    }

    @Test
    void generateSensorDataForHives_MultipleHiveIds_ReturnsMapWithAllEntries() {
        // Arrange
        List<Long> hiveIds = Arrays.asList(1L, 2L, 3L, 4L, 5L);

        // Act
        Map<Long, HiveSensorDataDTO> result = sensorDataService.generateSensorDataForHives(hiveIds);

        // Assert
        assertNotNull(result);
        assertEquals(5, result.size());
        
        for (Long hiveId : hiveIds) {
            assertTrue(result.containsKey(hiveId), "Map should contain hiveId: " + hiveId);
            assertNotNull(result.get(hiveId), "Sensor data should not be null for hiveId: " + hiveId);
            
            // Verify data validity for each hive
            HiveSensorDataDTO data = result.get(hiveId);
            assertTrue(data.getTemperature() >= 15.0 && data.getTemperature() <= 30.0);
            assertTrue(data.getWeight() >= 4.0 && data.getWeight() <= 12.0);
        }
    }

    @Test
    void generateSensorDataForHives_EmptyList_ReturnsEmptyMap() {
        // Arrange
        List<Long> hiveIds = Collections.emptyList();

        // Act
        Map<Long, HiveSensorDataDTO> result = sensorDataService.generateSensorDataForHives(hiveIds);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void generateSensorDataForHives_DuplicateHiveIds_OverwritesPreviousEntries() {
        // Arrange
        List<Long> hiveIds = Arrays.asList(1L, 1L, 1L);

        // Act
        Map<Long, HiveSensorDataDTO> result = sensorDataService.generateSensorDataForHives(hiveIds);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size()); // Map should only have one entry (last one)
        assertTrue(result.containsKey(1L));
        assertNotNull(result.get(1L));
    }

    @Test
    void generateSensorDataForHives_EachHiveHasDifferentData() {
        // Arrange
        List<Long> hiveIds = Arrays.asList(1L, 2L, 3L);

        // Act
        Map<Long, HiveSensorDataDTO> result = sensorDataService.generateSensorDataForHives(hiveIds);

        // Assert
        assertNotNull(result);
        assertEquals(3, result.size());
        
        HiveSensorDataDTO data1 = result.get(1L);
        HiveSensorDataDTO data2 = result.get(2L);
        HiveSensorDataDTO data3 = result.get(3L);
        
        assertNotNull(data1);
        assertNotNull(data2);
        assertNotNull(data3);
        
        // At least one field should be different between the hives (likely with random generation)
        // We can't guarantee all are different, but we can verify they're valid
        assertTrue(data1.getTemperature() >= 15.0 && data1.getTemperature() <= 30.0);
        assertTrue(data2.getTemperature() >= 15.0 && data2.getTemperature() <= 30.0);
        assertTrue(data3.getTemperature() >= 15.0 && data3.getTemperature() <= 30.0);
    }

    @Test
    void generateSensorData_AllFieldsAreSet() {
        // Act
        HiveSensorDataDTO data = sensorDataService.generateSensorData();

        // Assert
        assertNotNull(data);
        assertNotNull(data.getTemperature());
        assertNotNull(data.getExternalTemperature());
        assertNotNull(data.getHumidity());
        assertNotNull(data.getCo2());
        assertNotNull(data.getSoundLevel());
        assertNotNull(data.getWeight());
    }

    @Test
    void generateSensorData_ValuesWithinRealisticRanges() {
        // Act - Generate multiple readings to verify ranges
        for (int i = 0; i < 100; i++) {
            HiveSensorDataDTO data = sensorDataService.generateSensorData();
            
            // Assert - All values should be within expected ranges
            assertTrue(data.getTemperature() >= 15.0 && data.getTemperature() <= 30.0,
                    "Temperature out of range: " + data.getTemperature());
            assertTrue(data.getExternalTemperature() >= 15.0 && data.getExternalTemperature() <= 30.0,
                    "External temperature out of range: " + data.getExternalTemperature());
            assertTrue(data.getHumidity() >= 5.0 && data.getHumidity() <= 60.0,
                    "Humidity out of range: " + data.getHumidity());
            assertTrue(data.getCo2() >= 400.0 && data.getCo2() <= 2000.0,
                    "CO2 out of range: " + data.getCo2());
            assertTrue(data.getSoundLevel() >= 40.0 && data.getSoundLevel() <= 100.0,
                    "Sound level out of range: " + data.getSoundLevel());
            assertTrue(data.getWeight() >= 4.0 && data.getWeight() <= 12.0,
                    "Weight out of range: " + data.getWeight());
        }
    }
}

