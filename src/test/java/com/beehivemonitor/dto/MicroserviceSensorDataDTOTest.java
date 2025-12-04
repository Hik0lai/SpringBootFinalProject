package com.beehivemonitor.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for MicroserviceSensorDataDTO
 * Tests constructor, getters, setters, equals, hashCode, toString, and JSON serialization
 */
class MicroserviceSensorDataDTOTest {

    private MicroserviceSensorDataDTO dto;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        dto = new MicroserviceSensorDataDTO();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testNoArgsConstructor() {
        // Act
        MicroserviceSensorDataDTO emptyDto = new MicroserviceSensorDataDTO();

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
        double temperature = 35.5;
        double externalTemperature = 25.0;
        double humidity = 60.0;
        double co2 = 500.0;
        double soundLevel = 70.0;
        double weight = 20.5;

        // Act
        MicroserviceSensorDataDTO dto = new MicroserviceSensorDataDTO(
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
        double temperature = 35.5;
        double externalTemperature = 25.0;
        double humidity = 60.0;
        double co2 = 500.0;
        double soundLevel = 70.0;
        double weight = 20.5;

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
        MicroserviceSensorDataDTO dto1 = new MicroserviceSensorDataDTO(35.5, 25.0, 60.0, 500.0, 70.0, 20.5);
        MicroserviceSensorDataDTO dto2 = new MicroserviceSensorDataDTO(35.5, 25.0, 60.0, 500.0, 70.0, 20.5);

        // Act & Assert
        assertEquals(dto1, dto2);
    }

    @Test
    void testEquals_DifferentValues() {
        // Arrange
        MicroserviceSensorDataDTO dto1 = new MicroserviceSensorDataDTO(35.5, 25.0, 60.0, 500.0, 70.0, 20.5);
        MicroserviceSensorDataDTO dto2 = new MicroserviceSensorDataDTO(36.0, 26.0, 65.0, 550.0, 75.0, 21.0);

        // Act & Assert
        assertNotEquals(dto1, dto2);
    }

    @Test
    void testHashCode_SameValues() {
        // Arrange
        MicroserviceSensorDataDTO dto1 = new MicroserviceSensorDataDTO(35.5, 25.0, 60.0, 500.0, 70.0, 20.5);
        MicroserviceSensorDataDTO dto2 = new MicroserviceSensorDataDTO(35.5, 25.0, 60.0, 500.0, 70.0, 20.5);

        // Act & Assert
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    void testToString() {
        // Arrange
        dto.setTemperature(35.5);
        dto.setHumidity(60.0);

        // Act
        String toString = dto.toString();

        // Assert
        assertNotNull(toString);
    }

    @Test
    void testJsonSerialization() throws Exception {
        // Arrange
        dto.setTemperature(35.5);
        dto.setExternalTemperature(25.0);
        dto.setHumidity(60.0);
        dto.setCo2(500.0);
        dto.setSoundLevel(70.0);
        dto.setWeight(20.5);

        // Act
        String json = objectMapper.writeValueAsString(dto);
        MicroserviceSensorDataDTO deserialized = objectMapper.readValue(json, MicroserviceSensorDataDTO.class);

        // Assert
        assertNotNull(json);
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
                    "temperature": 35.5,
                    "externalTemperature": 25.0,
                    "humidity": 60.0,
                    "co2": 500.0,
                    "soundLevel": 70.0,
                    "weight": 20.5
                }
                """;

        // Act
        MicroserviceSensorDataDTO deserialized = objectMapper.readValue(json, MicroserviceSensorDataDTO.class);

        // Assert
        assertEquals(35.5, deserialized.getTemperature());
        assertEquals(25.0, deserialized.getExternalTemperature());
        assertEquals(60.0, deserialized.getHumidity());
        assertEquals(500.0, deserialized.getCo2());
        assertEquals(70.0, deserialized.getSoundLevel());
        assertEquals(20.5, deserialized.getWeight());
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
        MicroserviceSensorDataDTO dto = new MicroserviceSensorDataDTO(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
        
        assertEquals(0.0, dto.getTemperature());
        assertEquals(0.0, dto.getHumidity());
    }

    @Test
    void testVeryLargeValues() {
        // Test with very large values
        dto.setTemperature(1000.0);
        dto.setWeight(10000.0);
        
        assertEquals(1000.0, dto.getTemperature());
        assertEquals(10000.0, dto.getWeight());
    }
}

