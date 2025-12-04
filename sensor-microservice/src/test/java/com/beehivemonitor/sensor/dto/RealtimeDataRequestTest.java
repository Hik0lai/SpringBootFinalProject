package com.beehivemonitor.sensor.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for RealtimeDataRequest DTO
 * Tests constructor, getters, setters, equals, hashCode, toString, and JSON serialization
 */
class RealtimeDataRequestTest {

    private RealtimeDataRequest request;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        request = new RealtimeDataRequest();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testNoArgsConstructor() {
        // Act
        RealtimeDataRequest emptyRequest = new RealtimeDataRequest();

        // Assert
        assertNotNull(emptyRequest);
        assertNull(emptyRequest.getHiveIds());
    }

    @Test
    void testAllArgsConstructor() {
        // Arrange
        List<Long> hiveIds = Arrays.asList(1L, 2L, 3L);

        // Act
        RealtimeDataRequest request = new RealtimeDataRequest(hiveIds);

        // Assert
        assertNotNull(request.getHiveIds());
        assertEquals(3, request.getHiveIds().size());
        assertTrue(request.getHiveIds().contains(1L));
        assertTrue(request.getHiveIds().contains(2L));
        assertTrue(request.getHiveIds().contains(3L));
    }

    @Test
    void testGettersAndSetters() {
        // Arrange
        List<Long> hiveIds = Arrays.asList(1L, 2L, 3L);

        // Act
        request.setHiveIds(hiveIds);

        // Assert
        assertEquals(3, request.getHiveIds().size());
        assertTrue(request.getHiveIds().contains(1L));
        assertTrue(request.getHiveIds().contains(2L));
        assertTrue(request.getHiveIds().contains(3L));
    }

    @Test
    void testEquals_SameValues() {
        // Arrange
        List<Long> hiveIds1 = Arrays.asList(1L, 2L, 3L);
        List<Long> hiveIds2 = Arrays.asList(1L, 2L, 3L);
        
        RealtimeDataRequest request1 = new RealtimeDataRequest(hiveIds1);
        RealtimeDataRequest request2 = new RealtimeDataRequest(hiveIds2);

        // Act & Assert
        assertEquals(request1, request2);
        assertEquals(request2, request1);
    }

    @Test
    void testEquals_DifferentValues() {
        // Arrange
        List<Long> hiveIds1 = Arrays.asList(1L, 2L, 3L);
        List<Long> hiveIds2 = Arrays.asList(4L, 5L, 6L);
        
        RealtimeDataRequest request1 = new RealtimeDataRequest(hiveIds1);
        RealtimeDataRequest request2 = new RealtimeDataRequest(hiveIds2);

        // Act & Assert
        assertNotEquals(request1, request2);
    }

    @Test
    void testEquals_EmptyList() {
        // Arrange
        List<Long> emptyList1 = Collections.emptyList();
        List<Long> emptyList2 = Collections.emptyList();
        
        RealtimeDataRequest request1 = new RealtimeDataRequest(emptyList1);
        RealtimeDataRequest request2 = new RealtimeDataRequest(emptyList2);

        // Act & Assert
        assertEquals(request1, request2);
    }

    @Test
    void testHashCode_SameValues() {
        // Arrange
        List<Long> hiveIds1 = Arrays.asList(1L, 2L, 3L);
        List<Long> hiveIds2 = Arrays.asList(1L, 2L, 3L);
        
        RealtimeDataRequest request1 = new RealtimeDataRequest(hiveIds1);
        RealtimeDataRequest request2 = new RealtimeDataRequest(hiveIds2);

        // Act & Assert
        assertEquals(request1.hashCode(), request2.hashCode());
    }

    @Test
    void testToString() {
        // Arrange
        request.setHiveIds(Arrays.asList(1L, 2L, 3L));

        // Act
        String toString = request.toString();

        // Assert
        assertNotNull(toString);
    }

    @Test
    void testJsonSerialization() throws Exception {
        // Arrange
        request.setHiveIds(Arrays.asList(1L, 2L, 3L));

        // Act
        String json = objectMapper.writeValueAsString(request);
        RealtimeDataRequest deserialized = objectMapper.readValue(json, RealtimeDataRequest.class);

        // Assert
        assertNotNull(json);
        assertTrue(json.contains("hiveIds"));
        assertNotNull(deserialized.getHiveIds());
        assertEquals(3, deserialized.getHiveIds().size());
        assertTrue(deserialized.getHiveIds().contains(1L));
        assertTrue(deserialized.getHiveIds().contains(2L));
        assertTrue(deserialized.getHiveIds().contains(3L));
    }

    @Test
    void testJsonDeserialization() throws Exception {
        // Arrange
        String json = """
                {
                    "hiveIds": [1, 2, 3]
                }
                """;

        // Act
        RealtimeDataRequest deserialized = objectMapper.readValue(json, RealtimeDataRequest.class);

        // Assert
        assertNotNull(deserialized.getHiveIds());
        assertEquals(3, deserialized.getHiveIds().size());
        assertTrue(deserialized.getHiveIds().contains(1L));
        assertTrue(deserialized.getHiveIds().contains(2L));
        assertTrue(deserialized.getHiveIds().contains(3L));
    }

    @Test
    void testJsonDeserialization_EmptyList() throws Exception {
        // Arrange
        String json = """
                {
                    "hiveIds": []
                }
                """;

        // Act
        RealtimeDataRequest deserialized = objectMapper.readValue(json, RealtimeDataRequest.class);

        // Assert
        assertNotNull(deserialized.getHiveIds());
        assertTrue(deserialized.getHiveIds().isEmpty());
    }

    @Test
    void testJsonDeserialization_SingleHiveId() throws Exception {
        // Arrange
        String json = """
                {
                    "hiveIds": [1]
                }
                """;

        // Act
        RealtimeDataRequest deserialized = objectMapper.readValue(json, RealtimeDataRequest.class);

        // Assert
        assertNotNull(deserialized.getHiveIds());
        assertEquals(1, deserialized.getHiveIds().size());
        assertEquals(1L, deserialized.getHiveIds().get(0));
    }

    @Test
    void testJsonDeserialization_NullList() throws Exception {
        // Arrange
        String json = """
                {
                    "hiveIds": null
                }
                """;

        // Act
        RealtimeDataRequest deserialized = objectMapper.readValue(json, RealtimeDataRequest.class);

        // Assert
        assertNull(deserialized.getHiveIds());
    }
}

