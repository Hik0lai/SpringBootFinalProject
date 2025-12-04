package com.beehivemonitor.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for MicroserviceRealtimeResponse DTO
 * Tests constructor, getters, setters, equals, hashCode, toString, and JSON serialization
 */
class MicroserviceRealtimeResponseTest {

    private MicroserviceRealtimeResponse response;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        response = new MicroserviceRealtimeResponse();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testNoArgsConstructor() {
        // Act
        MicroserviceRealtimeResponse emptyResponse = new MicroserviceRealtimeResponse();

        // Assert
        assertNotNull(emptyResponse);
        assertNull(emptyResponse.getSensorData());
    }

    @Test
    void testAllArgsConstructor() {
        // Arrange
        UUID hiveId = UUID.randomUUID();
        MicroserviceSensorDataDTO sensorData = new MicroserviceSensorDataDTO(35.5, 25.0, 60.0, 500.0, 70.0, 20.5);
        Map<UUID, MicroserviceSensorDataDTO> sensorDataMap = new HashMap<>();
        sensorDataMap.put(hiveId, sensorData);

        // Act
        MicroserviceRealtimeResponse response = new MicroserviceRealtimeResponse(sensorDataMap);

        // Assert
        assertNotNull(response.getSensorData());
        assertEquals(1, response.getSensorData().size());
        assertTrue(response.getSensorData().containsKey(hiveId));
        assertEquals(sensorData, response.getSensorData().get(hiveId));
    }

    @Test
    void testGettersAndSetters() {
        // Arrange
        UUID hiveId = UUID.randomUUID();
        MicroserviceSensorDataDTO sensorData = new MicroserviceSensorDataDTO(35.5, 25.0, 60.0, 500.0, 70.0, 20.5);
        Map<UUID, MicroserviceSensorDataDTO> sensorDataMap = new HashMap<>();
        sensorDataMap.put(hiveId, sensorData);

        // Act
        response.setSensorData(sensorDataMap);

        // Assert
        assertNotNull(response.getSensorData());
        assertEquals(1, response.getSensorData().size());
        assertTrue(response.getSensorData().containsKey(hiveId));
    }

    @Test
    void testEquals_SameValues() {
        // Arrange
        UUID hiveId = UUID.randomUUID();
        MicroserviceSensorDataDTO sensorData = new MicroserviceSensorDataDTO(35.5, 25.0, 60.0, 500.0, 70.0, 20.5);
        
        Map<UUID, MicroserviceSensorDataDTO> map1 = new HashMap<>();
        map1.put(hiveId, sensorData);
        
        Map<UUID, MicroserviceSensorDataDTO> map2 = new HashMap<>();
        map2.put(hiveId, sensorData);
        
        MicroserviceRealtimeResponse response1 = new MicroserviceRealtimeResponse(map1);
        MicroserviceRealtimeResponse response2 = new MicroserviceRealtimeResponse(map2);

        // Act & Assert
        assertEquals(response1, response2);
    }

    @Test
    void testHashCode_SameValues() {
        // Arrange
        UUID hiveId = UUID.randomUUID();
        MicroserviceSensorDataDTO sensorData = new MicroserviceSensorDataDTO(35.5, 25.0, 60.0, 500.0, 70.0, 20.5);
        
        Map<UUID, MicroserviceSensorDataDTO> map1 = new HashMap<>();
        map1.put(hiveId, sensorData);
        
        Map<UUID, MicroserviceSensorDataDTO> map2 = new HashMap<>();
        map2.put(hiveId, sensorData);
        
        MicroserviceRealtimeResponse response1 = new MicroserviceRealtimeResponse(map1);
        MicroserviceRealtimeResponse response2 = new MicroserviceRealtimeResponse(map2);

        // Act & Assert
        assertEquals(response1.hashCode(), response2.hashCode());
    }

    @Test
    void testToString() {
        // Arrange
        UUID hiveId = UUID.randomUUID();
        MicroserviceSensorDataDTO sensorData = new MicroserviceSensorDataDTO(35.5, 25.0, 60.0, 500.0, 70.0, 20.5);
        Map<UUID, MicroserviceSensorDataDTO> sensorDataMap = new HashMap<>();
        sensorDataMap.put(hiveId, sensorData);
        response.setSensorData(sensorDataMap);

        // Act
        String toString = response.toString();

        // Assert
        assertNotNull(toString);
    }

    @Test
    void testJsonSerialization() throws Exception {
        // Arrange
        UUID hiveId = UUID.randomUUID();
        MicroserviceSensorDataDTO sensorData = new MicroserviceSensorDataDTO(35.5, 25.0, 60.0, 500.0, 70.0, 20.5);
        Map<UUID, MicroserviceSensorDataDTO> sensorDataMap = new HashMap<>();
        sensorDataMap.put(hiveId, sensorData);
        response.setSensorData(sensorDataMap);

        // Act
        String json = objectMapper.writeValueAsString(response);
        MicroserviceRealtimeResponse deserialized = objectMapper.readValue(json, MicroserviceRealtimeResponse.class);

        // Assert
        assertNotNull(json);
        assertNotNull(deserialized.getSensorData());
        assertEquals(1, deserialized.getSensorData().size());
        assertTrue(deserialized.getSensorData().containsKey(hiveId));
    }

    @Test
    void testJsonDeserialization() throws Exception {
        // Arrange
        UUID hiveId = UUID.randomUUID();
        String json = String.format("""
                {
                    "sensorData": {
                        "%s": {
                            "temperature": 35.5,
                            "externalTemperature": 25.0,
                            "humidity": 60.0,
                            "co2": 500.0,
                            "soundLevel": 70.0,
                            "weight": 20.5
                        }
                    }
                }
                """, hiveId);

        // Act
        MicroserviceRealtimeResponse deserialized = objectMapper.readValue(json, MicroserviceRealtimeResponse.class);

        // Assert
        assertNotNull(deserialized.getSensorData());
        assertEquals(1, deserialized.getSensorData().size());
        assertTrue(deserialized.getSensorData().containsKey(hiveId));
        
        MicroserviceSensorDataDTO data = deserialized.getSensorData().get(hiveId);
        assertNotNull(data);
        assertEquals(35.5, data.getTemperature());
        assertEquals(60.0, data.getHumidity());
    }

    @Test
    void testJsonDeserialization_EmptyMap() throws Exception {
        // Arrange
        String json = """
                {
                    "sensorData": {}
                }
                """;

        // Act
        MicroserviceRealtimeResponse deserialized = objectMapper.readValue(json, MicroserviceRealtimeResponse.class);

        // Assert
        assertNotNull(deserialized.getSensorData());
        assertTrue(deserialized.getSensorData().isEmpty());
    }

    @Test
    void testMultipleHives() {
        // Arrange
        UUID hiveId1 = UUID.randomUUID();
        UUID hiveId2 = UUID.randomUUID();
        MicroserviceSensorDataDTO data1 = new MicroserviceSensorDataDTO(35.5, 25.0, 60.0, 500.0, 70.0, 20.5);
        MicroserviceSensorDataDTO data2 = new MicroserviceSensorDataDTO(36.0, 26.0, 65.0, 550.0, 75.0, 21.0);
        
        Map<UUID, MicroserviceSensorDataDTO> sensorDataMap = new HashMap<>();
        sensorDataMap.put(hiveId1, data1);
        sensorDataMap.put(hiveId2, data2);
        response.setSensorData(sensorDataMap);

        // Assert
        assertEquals(2, response.getSensorData().size());
        assertTrue(response.getSensorData().containsKey(hiveId1));
        assertTrue(response.getSensorData().containsKey(hiveId2));
    }
}

