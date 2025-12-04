package com.beehivemonitor.sensor.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for HiveSensorDataDTO
 * Tests constructor, getters, setters, equals, hashCode, toString, and JSON serialization
 */
class HiveSensorDataDTOTest {

    private HiveSensorDataDTO dto;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        dto = new HiveSensorDataDTO();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testNoArgsConstructor() {
        // Act
        HiveSensorDataDTO emptyDto = new HiveSensorDataDTO();

        // Assert
        assertNotNull(emptyDto);
        assertEquals(0.0, emptyDto.getTemperature());
        assertEquals(0.0, emptyDto.getExternalTemperature());
        assertEquals(0.0, emptyDto.getHumidity());
        assertEquals(0.0, emptyDto.getCo2());
        assertEquals(0.0, emptyDto.getSoundLevel());
        assertEquals(0.0, emptyDto.getWeight());
    }

    @Test
    void testAllArgsConstructor() {
        // Arrange
        double temperature = 25.5;
        double externalTemperature = 24.0;
        double humidity = 55.0;
        double co2 = 850.0;
        double soundLevel = 65.0;
        double weight = 8.5;

        // Act
        HiveSensorDataDTO dto = new HiveSensorDataDTO(
                temperature, externalTemperature, humidity, co2, soundLevel, weight
        );

        // Assert
        assertEquals(temperature, dto.getTemperature());
        assertEquals(externalTemperature, dto.getExternalTemperature());
        assertEquals(humidity, dto.getHumidity());
        assertEquals(co2, dto.getCo2());
        assertEquals(soundLevel, dto.getSoundLevel());
        assertEquals(weight, dto.getWeight());
    }

    @Test
    void testGettersAndSetters() {
        // Arrange
        double temperature = 25.5;
        double externalTemperature = 24.0;
        double humidity = 55.0;
        double co2 = 850.0;
        double soundLevel = 65.0;
        double weight = 8.5;

        // Act
        dto.setTemperature(temperature);
        dto.setExternalTemperature(externalTemperature);
        dto.setHumidity(humidity);
        dto.setCo2(co2);
        dto.setSoundLevel(soundLevel);
        dto.setWeight(weight);

        // Assert
        assertEquals(temperature, dto.getTemperature());
        assertEquals(externalTemperature, dto.getExternalTemperature());
        assertEquals(humidity, dto.getHumidity());
        assertEquals(co2, dto.getCo2());
        assertEquals(soundLevel, dto.getSoundLevel());
        assertEquals(weight, dto.getWeight());
    }

    @Test
    void testEquals_SameValues() {
        // Arrange
        HiveSensorDataDTO dto1 = new HiveSensorDataDTO(25.5, 24.0, 55.0, 850.0, 65.0, 8.5);
        HiveSensorDataDTO dto2 = new HiveSensorDataDTO(25.5, 24.0, 55.0, 850.0, 65.0, 8.5);

        // Act & Assert
        assertEquals(dto1, dto2);
        assertEquals(dto2, dto1);
    }

    @Test
    void testEquals_DifferentValues() {
        // Arrange
        HiveSensorDataDTO dto1 = new HiveSensorDataDTO(25.5, 24.0, 55.0, 850.0, 65.0, 8.5);
        HiveSensorDataDTO dto2 = new HiveSensorDataDTO(26.0, 25.0, 56.0, 900.0, 70.0, 9.0);

        // Act & Assert
        assertNotEquals(dto1, dto2);
    }

    @Test
    void testHashCode_SameValues() {
        // Arrange
        HiveSensorDataDTO dto1 = new HiveSensorDataDTO(25.5, 24.0, 55.0, 850.0, 65.0, 8.5);
        HiveSensorDataDTO dto2 = new HiveSensorDataDTO(25.5, 24.0, 55.0, 850.0, 65.0, 8.5);

        // Act & Assert
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    void testToString() {
        // Arrange
        dto.setTemperature(25.5);
        dto.setHumidity(55.0);
        dto.setCo2(850.0);

        // Act
        String toString = dto.toString();

        // Assert
        assertNotNull(toString);
    }

    @Test
    void testJsonSerialization() throws Exception {
        // Arrange
        dto.setTemperature(25.5);
        dto.setExternalTemperature(24.0);
        dto.setHumidity(55.0);
        dto.setCo2(850.0);
        dto.setSoundLevel(65.0);
        dto.setWeight(8.5);

        // Act
        String json = objectMapper.writeValueAsString(dto);
        HiveSensorDataDTO deserialized = objectMapper.readValue(json, HiveSensorDataDTO.class);

        // Assert
        assertNotNull(json);
        assertTrue(json.contains("25.5"));
        assertEquals(dto.getTemperature(), deserialized.getTemperature());
        assertEquals(dto.getExternalTemperature(), deserialized.getExternalTemperature());
        assertEquals(dto.getHumidity(), deserialized.getHumidity());
        assertEquals(dto.getCo2(), deserialized.getCo2());
        assertEquals(dto.getSoundLevel(), deserialized.getSoundLevel());
        assertEquals(dto.getWeight(), deserialized.getWeight());
    }

    @Test
    void testJsonDeserialization() throws Exception {
        // Arrange
        String json = """
                {
                    "temperature": 25.5,
                    "externalTemperature": 24.0,
                    "humidity": 55.0,
                    "co2": 850.0,
                    "soundLevel": 65.0,
                    "weight": 8.5
                }
                """;

        // Act
        HiveSensorDataDTO deserialized = objectMapper.readValue(json, HiveSensorDataDTO.class);

        // Assert
        assertEquals(25.5, deserialized.getTemperature());
        assertEquals(24.0, deserialized.getExternalTemperature());
        assertEquals(55.0, deserialized.getHumidity());
        assertEquals(850.0, deserialized.getCo2());
        assertEquals(65.0, deserialized.getSoundLevel());
        assertEquals(8.5, deserialized.getWeight());
    }

    @Test
    void testNegativeValues() {
        // Test with negative values (edge case)
        dto.setTemperature(-10.0);
        dto.setExternalTemperature(-5.0);
        
        assertEquals(-10.0, dto.getTemperature());
        assertEquals(-5.0, dto.getExternalTemperature());
    }

    @Test
    void testZeroValues() {
        // Test with zero values
        HiveSensorDataDTO dto = new HiveSensorDataDTO(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
        
        assertEquals(0.0, dto.getTemperature());
        assertEquals(0.0, dto.getHumidity());
        assertEquals(0.0, dto.getCo2());
    }
}

