package com.beehivemonitor.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for MicroserviceRealtimeRequest DTO
 * Tests constructor, getters, setters, equals, hashCode, toString, and JSON serialization
 */
class MicroserviceRealtimeRequestTest {

    private MicroserviceRealtimeRequest request;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        request = new MicroserviceRealtimeRequest();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testNoArgsConstructor() {
        // Act
        MicroserviceRealtimeRequest emptyRequest = new MicroserviceRealtimeRequest();

        // Assert
        assertNotNull(emptyRequest);
        assertNull(emptyRequest.getHiveIds());
    }

    @Test
    void testAllArgsConstructor() {
        // Arrange
        UUID hiveId1 = UUID.randomUUID();
        UUID hiveId2 = UUID.randomUUID();
        List<UUID> hiveIds = Arrays.asList(hiveId1, hiveId2);

        // Act
        MicroserviceRealtimeRequest request = new MicroserviceRealtimeRequest(hiveIds);

        // Assert
        assertNotNull(request.getHiveIds());
        assertEquals(2, request.getHiveIds().size());
        assertTrue(request.getHiveIds().contains(hiveId1));
        assertTrue(request.getHiveIds().contains(hiveId2));
    }

    @Test
    void testGettersAndSetters() {
        // Arrange
        UUID hiveId1 = UUID.randomUUID();
        UUID hiveId2 = UUID.randomUUID();
        List<UUID> hiveIds = Arrays.asList(hiveId1, hiveId2);

        // Act
        request.setHiveIds(hiveIds);

        // Assert
        assertEquals(2, request.getHiveIds().size());
        assertTrue(request.getHiveIds().contains(hiveId1));
        assertTrue(request.getHiveIds().contains(hiveId2));
    }

    @Test
    void testEquals_SameValues() {
        // Arrange
        UUID hiveId1 = UUID.randomUUID();
        UUID hiveId2 = UUID.randomUUID();
        List<UUID> hiveIds1 = Arrays.asList(hiveId1, hiveId2);
        List<UUID> hiveIds2 = Arrays.asList(hiveId1, hiveId2);
        
        MicroserviceRealtimeRequest request1 = new MicroserviceRealtimeRequest(hiveIds1);
        MicroserviceRealtimeRequest request2 = new MicroserviceRealtimeRequest(hiveIds2);

        // Act & Assert
        assertEquals(request1, request2);
    }

    @Test
    void testEquals_EmptyList() {
        // Arrange
        List<UUID> emptyList1 = Collections.emptyList();
        List<UUID> emptyList2 = Collections.emptyList();
        
        MicroserviceRealtimeRequest request1 = new MicroserviceRealtimeRequest(emptyList1);
        MicroserviceRealtimeRequest request2 = new MicroserviceRealtimeRequest(emptyList2);

        // Act & Assert
        assertEquals(request1, request2);
    }

    @Test
    void testHashCode_SameValues() {
        // Arrange
        UUID hiveId1 = UUID.randomUUID();
        UUID hiveId2 = UUID.randomUUID();
        List<UUID> hiveIds1 = Arrays.asList(hiveId1, hiveId2);
        List<UUID> hiveIds2 = Arrays.asList(hiveId1, hiveId2);
        
        MicroserviceRealtimeRequest request1 = new MicroserviceRealtimeRequest(hiveIds1);
        MicroserviceRealtimeRequest request2 = new MicroserviceRealtimeRequest(hiveIds2);

        // Act & Assert
        assertEquals(request1.hashCode(), request2.hashCode());
    }

    @Test
    void testToString() {
        // Arrange
        UUID hiveId = UUID.randomUUID();
        request.setHiveIds(Collections.singletonList(hiveId));

        // Act
        String toString = request.toString();

        // Assert
        assertNotNull(toString);
    }

    @Test
    void testJsonSerialization() throws Exception {
        // Arrange
        UUID hiveId1 = UUID.randomUUID();
        UUID hiveId2 = UUID.randomUUID();
        request.setHiveIds(Arrays.asList(hiveId1, hiveId2));

        // Act
        String json = objectMapper.writeValueAsString(request);
        MicroserviceRealtimeRequest deserialized = objectMapper.readValue(json, MicroserviceRealtimeRequest.class);

        // Assert
        assertNotNull(json);
        assertNotNull(deserialized.getHiveIds());
        assertEquals(2, deserialized.getHiveIds().size());
        assertTrue(deserialized.getHiveIds().contains(hiveId1));
        assertTrue(deserialized.getHiveIds().contains(hiveId2));
    }

    @Test
    void testJsonDeserialization() throws Exception {
        // Arrange
        UUID hiveId1 = UUID.randomUUID();
        UUID hiveId2 = UUID.randomUUID();
        String json = String.format("""
                {
                    "hiveIds": ["%s", "%s"]
                }
                """, hiveId1, hiveId2);

        // Act
        MicroserviceRealtimeRequest deserialized = objectMapper.readValue(json, MicroserviceRealtimeRequest.class);

        // Assert
        assertNotNull(deserialized.getHiveIds());
        assertEquals(2, deserialized.getHiveIds().size());
        assertTrue(deserialized.getHiveIds().contains(hiveId1));
        assertTrue(deserialized.getHiveIds().contains(hiveId2));
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
        MicroserviceRealtimeRequest deserialized = objectMapper.readValue(json, MicroserviceRealtimeRequest.class);

        // Assert
        assertNotNull(deserialized.getHiveIds());
        assertTrue(deserialized.getHiveIds().isEmpty());
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
        MicroserviceRealtimeRequest deserialized = objectMapper.readValue(json, MicroserviceRealtimeRequest.class);

        // Assert
        assertNull(deserialized.getHiveIds());
    }

    @Test
    void testSingleHiveId() {
        // Arrange
        UUID hiveId = UUID.randomUUID();
        request.setHiveIds(Collections.singletonList(hiveId));

        // Assert
        assertEquals(1, request.getHiveIds().size());
        assertEquals(hiveId, request.getHiveIds().get(0));
    }
}

