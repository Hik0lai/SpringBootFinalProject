package com.beehivemonitor.sensor.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for RealtimeDataResponse DTO
 * Tests constructor, getters, setters, equals, hashCode, toString, and JSON serialization
 */
class RealtimeDataResponseTest {

    private RealtimeDataResponse response;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        response = new RealtimeDataResponse();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testNoArgsConstructor() {
        // Act
        RealtimeDataResponse emptyResponse = new RealtimeDataResponse();

        // Assert
        assertNotNull(emptyResponse);
        assertNull(emptyResponse.getSensorData());
    }

    @Test
    void testAllArgsConstructor() {
        // Arrange
        HiveSensorDataDTO sensorData1 = new HiveSensorDataDTO(25.5, 24.0, 55.0, 850.0, 65.0, 8.5);
        Map<Long, HiveSensorDataDTO> sensorDataMap = new HashMap<>();
        sensorDataMap.put(1L, sensorData1);

        // Act
        RealtimeDataResponse response = new RealtimeDataResponse(sensorDataMap);

        // Assert
        assertNotNull(response.getSensorData());
        assertEquals(1, response.getSensorData().size());
        assertTrue(response.getSensorData().containsKey(1L));
        assertEquals(sensorData1, response.getSensorData().get(1L));
    }

    @Test
    void testGettersAndSetters() {
        // Arrange
        HiveSensorDataDTO sensorData = new HiveSensorDataDTO(25.5, 24.0, 55.0, 850.0, 65.0, 8.5);
        Map<Long, HiveSensorDataDTO> sensorDataMap = new HashMap<>();
        sensorDataMap.put(1L, sensorData);

        // Act
        response.setSensorData(sensorDataMap);

        // Assert
        assertNotNull(response.getSensorData());
        assertEquals(1, response.getSensorData().size());
        assertTrue(response.getSensorData().containsKey(1L));
        assertEquals(sensorData, response.getSensorData().get(1L));
    }

    @Test
    void testEquals_SameValues() {
        // Arrange
        HiveSensorDataDTO sensorData = new HiveSensorDataDTO(25.5, 24.0, 55.0, 850.0, 65.0, 8.5);
        
        Map<Long, HiveSensorDataDTO> map1 = new HashMap<>();
        map1.put(1L, sensorData);
        
        Map<Long, HiveSensorDataDTO> map2 = new HashMap<>();
        map2.put(1L, sensorData);
        
        RealtimeDataResponse response1 = new RealtimeDataResponse(map1);
        RealtimeDataResponse response2 = new RealtimeDataResponse(map2);

        // Act & Assert
        assertEquals(response1, response2);
        assertEquals(response2, response1);
    }

    @Test
    void testEquals_DifferentValues() {
        // Arrange
        HiveSensorDataDTO sensorData1 = new HiveSensorDataDTO(25.5, 24.0, 55.0, 850.0, 65.0, 8.5);
        HiveSensorDataDTO sensorData2 = new HiveSensorDataDTO(26.0, 25.0, 56.0, 900.0, 70.0, 9.0);
        
        Map<Long, HiveSensorDataDTO> map1 = new HashMap<>();
        map1.put(1L, sensorData1);
        
        Map<Long, HiveSensorDataDTO> map2 = new HashMap<>();
        map2.put(1L, sensorData2);
        
        RealtimeDataResponse response1 = new RealtimeDataResponse(map1);
        RealtimeDataResponse response2 = new RealtimeDataResponse(map2);

        // Act & Assert
        assertNotEquals(response1, response2);
    }

    @Test
    void testHashCode_SameValues() {
        // Arrange
        HiveSensorDataDTO sensorData = new HiveSensorDataDTO(25.5, 24.0, 55.0, 850.0, 65.0, 8.5);
        
        Map<Long, HiveSensorDataDTO> map1 = new HashMap<>();
        map1.put(1L, sensorData);
        
        Map<Long, HiveSensorDataDTO> map2 = new HashMap<>();
        map2.put(1L, sensorData);
        
        RealtimeDataResponse response1 = new RealtimeDataResponse(map1);
        RealtimeDataResponse response2 = new RealtimeDataResponse(map2);

        // Act & Assert
        assertEquals(response1.hashCode(), response2.hashCode());
    }

    @Test
    void testToString() {
        // Arrange
        HiveSensorDataDTO sensorData = new HiveSensorDataDTO(25.5, 24.0, 55.0, 850.0, 65.0, 8.5);
        Map<Long, HiveSensorDataDTO> sensorDataMap = new HashMap<>();
        sensorDataMap.put(1L, sensorData);
        response.setSensorData(sensorDataMap);

        // Act
        String toString = response.toString();

        // Assert
        assertNotNull(toString);
    }

    @Test
    void testJsonSerialization() throws Exception {
        // Arrange
        HiveSensorDataDTO sensorData = new HiveSensorDataDTO(25.5, 24.0, 55.0, 850.0, 65.0, 8.5);
        Map<Long, HiveSensorDataDTO> sensorDataMap = new HashMap<>();
        sensorDataMap.put(1L, sensorData);
        response.setSensorData(sensorDataMap);

        // Act
        String json = objectMapper.writeValueAsString(response);
        RealtimeDataResponse deserialized = objectMapper.readValue(json, RealtimeDataResponse.class);

        // Assert
        assertNotNull(json);
        assertTrue(json.contains("sensorData"));
        assertNotNull(deserialized.getSensorData());
        assertEquals(1, deserialized.getSensorData().size());
        assertTrue(deserialized.getSensorData().containsKey(1L));
        
        HiveSensorDataDTO deserializedData = deserialized.getSensorData().get(1L);
        assertEquals(25.5, deserializedData.getTemperature());
        assertEquals(55.0, deserializedData.getHumidity());
    }

    @Test
    void testJsonDeserialization() throws Exception {
        // Arrange
        String json = """
                {
                    "sensorData": {
                        "1": {
                            "temperature": 25.5,
                            "externalTemperature": 24.0,
                            "humidity": 55.0,
                            "co2": 850.0,
                            "soundLevel": 65.0,
                            "weight": 8.5
                        }
                    }
                }
                """;

        // Act
        RealtimeDataResponse deserialized = objectMapper.readValue(json, RealtimeDataResponse.class);

        // Assert
        assertNotNull(deserialized.getSensorData());
        assertEquals(1, deserialized.getSensorData().size());
        assertTrue(deserialized.getSensorData().containsKey(1L));
        
        HiveSensorDataDTO data = deserialized.getSensorData().get(1L);
        assertNotNull(data);
        assertEquals(25.5, data.getTemperature());
        assertEquals(24.0, data.getExternalTemperature());
        assertEquals(55.0, data.getHumidity());
        assertEquals(850.0, data.getCo2());
        assertEquals(65.0, data.getSoundLevel());
        assertEquals(8.5, data.getWeight());
    }

    @Test
    void testJsonDeserialization_MultipleHives() throws Exception {
        // Arrange
        String json = """
                {
                    "sensorData": {
                        "1": {
                            "temperature": 25.5,
                            "externalTemperature": 24.0,
                            "humidity": 55.0,
                            "co2": 850.0,
                            "soundLevel": 65.0,
                            "weight": 8.5
                        },
                        "2": {
                            "temperature": 26.0,
                            "externalTemperature": 25.0,
                            "humidity": 56.0,
                            "co2": 900.0,
                            "soundLevel": 70.0,
                            "weight": 9.0
                        }
                    }
                }
                """;

        // Act
        RealtimeDataResponse deserialized = objectMapper.readValue(json, RealtimeDataResponse.class);

        // Assert
        assertNotNull(deserialized.getSensorData());
        assertEquals(2, deserialized.getSensorData().size());
        assertTrue(deserialized.getSensorData().containsKey(1L));
        assertTrue(deserialized.getSensorData().containsKey(2L));
        
        assertEquals(25.5, deserialized.getSensorData().get(1L).getTemperature());
        assertEquals(26.0, deserialized.getSensorData().get(2L).getTemperature());
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
        RealtimeDataResponse deserialized = objectMapper.readValue(json, RealtimeDataResponse.class);

        // Assert
        assertNotNull(deserialized.getSensorData());
        assertTrue(deserialized.getSensorData().isEmpty());
    }

    @Test
    void testMultipleHives() {
        // Arrange
        HiveSensorDataDTO data1 = new HiveSensorDataDTO(25.5, 24.0, 55.0, 850.0, 65.0, 8.5);
        HiveSensorDataDTO data2 = new HiveSensorDataDTO(26.0, 25.0, 56.0, 900.0, 70.0, 9.0);
        HiveSensorDataDTO data3 = new HiveSensorDataDTO(27.0, 26.0, 57.0, 950.0, 75.0, 9.5);
        
        Map<Long, HiveSensorDataDTO> sensorDataMap = new HashMap<>();
        sensorDataMap.put(1L, data1);
        sensorDataMap.put(2L, data2);
        sensorDataMap.put(3L, data3);
        response.setSensorData(sensorDataMap);

        // Assert
        assertEquals(3, response.getSensorData().size());
        assertTrue(response.getSensorData().containsKey(1L));
        assertTrue(response.getSensorData().containsKey(2L));
        assertTrue(response.getSensorData().containsKey(3L));
    }
}

