package com.beehivemonitor.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for SensorReadingDTO
 * Tests constructor, getters, setters, equals, hashCode, toString, and JSON serialization
 */
class SensorReadingDTOTest {

    private SensorReadingDTO dto;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        dto = new SensorReadingDTO();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testNoArgsConstructor() {
        // Act
        SensorReadingDTO emptyDto = new SensorReadingDTO();

        // Assert
        assertNotNull(emptyDto);
        assertNull(emptyDto.getType());
        assertNull(emptyDto.getValue());
        assertNull(emptyDto.getUnit());
    }

    @Test
    void testAllArgsConstructor() {
        // Arrange
        String type = "TEMPERATURE";
        Double value = 25.5;
        String unit = "°C";

        // Act
        SensorReadingDTO dto = new SensorReadingDTO(type, value, unit);

        // Assert
        assertEquals(type, dto.getType());
        assertEquals(value, dto.getValue());
        assertEquals(unit, dto.getUnit());
    }

    @Test
    void testGettersAndSetters() {
        // Arrange
        String type = "TEMPERATURE";
        Double value = 25.5;
        String unit = "°C";

        // Act
        dto.setType(type);
        dto.setValue(value);
        dto.setUnit(unit);

        // Assert
        assertEquals(type, dto.getType());
        assertEquals(value, dto.getValue());
        assertEquals(unit, dto.getUnit());
    }

    @Test
    void testEquals_SameValues() {
        // Arrange
        SensorReadingDTO dto1 = new SensorReadingDTO("TEMPERATURE", 25.5, "°C");
        SensorReadingDTO dto2 = new SensorReadingDTO("TEMPERATURE", 25.5, "°C");

        // Act & Assert
        assertEquals(dto1, dto2);
        assertEquals(dto2, dto1);
    }

    @Test
    void testEquals_DifferentValues() {
        // Arrange
        SensorReadingDTO dto1 = new SensorReadingDTO("TEMPERATURE", 25.5, "°C");
        SensorReadingDTO dto2 = new SensorReadingDTO("HUMIDITY", 60.0, "%");

        // Act & Assert
        assertNotEquals(dto1, dto2);
    }

    @Test
    void testHashCode_SameValues() {
        // Arrange
        SensorReadingDTO dto1 = new SensorReadingDTO("TEMPERATURE", 25.5, "°C");
        SensorReadingDTO dto2 = new SensorReadingDTO("TEMPERATURE", 25.5, "°C");

        // Act & Assert
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    void testToString() {
        // Arrange
        dto.setType("TEMPERATURE");
        dto.setValue(25.5);
        dto.setUnit("°C");

        // Act
        String toString = dto.toString();

        // Assert
        assertNotNull(toString);
        assertTrue(toString.contains("TEMPERATURE") || toString.contains("25.5"));
    }

    @Test
    void testJsonSerialization() throws Exception {
        // Arrange
        dto.setType("TEMPERATURE");
        dto.setValue(25.5);
        dto.setUnit("°C");

        // Act
        String json = objectMapper.writeValueAsString(dto);
        SensorReadingDTO deserialized = objectMapper.readValue(json, SensorReadingDTO.class);

        // Assert
        assertNotNull(json);
        assertTrue(json.contains("TEMPERATURE"));
        assertEquals(dto.getType(), deserialized.getType());
        assertEquals(dto.getValue(), deserialized.getValue());
        assertEquals(dto.getUnit(), deserialized.getUnit());
    }

    @Test
    void testJsonDeserialization() throws Exception {
        // Arrange
        String json = """
                {
                    "type": "TEMPERATURE",
                    "value": 25.5,
                    "unit": "°C"
                }
                """;

        // Act
        SensorReadingDTO deserialized = objectMapper.readValue(json, SensorReadingDTO.class);

        // Assert
        assertEquals("TEMPERATURE", deserialized.getType());
        assertEquals(25.5, deserialized.getValue());
        assertEquals("°C", deserialized.getUnit());
    }

    @Test
    void testDifferentSensorTypes() {
        // Test different sensor types
        dto.setType("TEMPERATURE");
        assertEquals("TEMPERATURE", dto.getType());

        dto.setType("HUMIDITY");
        assertEquals("HUMIDITY", dto.getType());

        dto.setType("CO2");
        assertEquals("CO2", dto.getType());

        dto.setType("SOUND_LEVEL");
        assertEquals("SOUND_LEVEL", dto.getType());

        dto.setType("WEIGHT");
        assertEquals("WEIGHT", dto.getType());
    }

    @Test
    void testNullValues() {
        // Arrange
        SensorReadingDTO dto1 = new SensorReadingDTO(null, null, null);
        SensorReadingDTO dto2 = new SensorReadingDTO(null, null, null);

        // Act & Assert
        assertEquals(dto1, dto2);
    }
}

