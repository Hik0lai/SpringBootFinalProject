package com.beehivemonitor.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for NotificationResponse DTO
 * Tests constructor, getters, setters, equals, hashCode, toString, and JSON serialization
 */
class NotificationResponseTest {

    private NotificationResponse response;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        response = new NotificationResponse();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testNoArgsConstructor() {
        // Act
        NotificationResponse emptyResponse = new NotificationResponse();

        // Assert
        assertNotNull(emptyResponse);
        assertNull(emptyResponse.getNotificationId());
        assertNull(emptyResponse.getSuccess());
        assertNull(emptyResponse.getMessage());
        assertNull(emptyResponse.getStatus());
    }

    @Test
    void testAllArgsConstructor() {
        // Arrange
        Long notificationId = 1L;
        Boolean success = true;
        String message = "Notification sent successfully";
        String status = "SENT";

        // Act
        NotificationResponse response = new NotificationResponse(notificationId, success, message, status);

        // Assert
        assertEquals(notificationId, response.getNotificationId());
        assertEquals(success, response.getSuccess());
        assertEquals(message, response.getMessage());
        assertEquals(status, response.getStatus());
    }

    @Test
    void testGettersAndSetters() {
        // Arrange
        Long notificationId = 1L;
        Boolean success = true;
        String message = "Notification sent successfully";
        String status = "SENT";

        // Act
        response.setNotificationId(notificationId);
        response.setSuccess(success);
        response.setMessage(message);
        response.setStatus(status);

        // Assert
        assertEquals(notificationId, response.getNotificationId());
        assertEquals(success, response.getSuccess());
        assertEquals(message, response.getMessage());
        assertEquals(status, response.getStatus());
    }

    @Test
    void testEquals_SameValues() {
        // Arrange
        NotificationResponse response1 = new NotificationResponse(1L, true, "Success", "SENT");
        NotificationResponse response2 = new NotificationResponse(1L, true, "Success", "SENT");

        // Act & Assert
        assertEquals(response1, response2);
    }

    @Test
    void testEquals_DifferentValues() {
        // Arrange
        NotificationResponse response1 = new NotificationResponse(1L, true, "Success", "SENT");
        NotificationResponse response2 = new NotificationResponse(2L, false, "Failed", "FAILED");

        // Act & Assert
        assertNotEquals(response1, response2);
    }

    @Test
    void testHashCode_SameValues() {
        // Arrange
        NotificationResponse response1 = new NotificationResponse(1L, true, "Success", "SENT");
        NotificationResponse response2 = new NotificationResponse(1L, true, "Success", "SENT");

        // Act & Assert
        assertEquals(response1.hashCode(), response2.hashCode());
    }

    @Test
    void testToString() {
        // Arrange
        response.setNotificationId(1L);
        response.setSuccess(true);
        response.setMessage("Notification sent successfully");
        response.setStatus("SENT");

        // Act
        String toString = response.toString();

        // Assert
        assertNotNull(toString);
    }

    @Test
    void testJsonSerialization() throws Exception {
        // Arrange
        response.setNotificationId(1L);
        response.setSuccess(true);
        response.setMessage("Notification sent successfully");
        response.setStatus("SENT");

        // Act
        String json = objectMapper.writeValueAsString(response);
        NotificationResponse deserialized = objectMapper.readValue(json, NotificationResponse.class);

        // Assert
        assertNotNull(json);
        assertTrue(json.contains("SENT"));
        assertEquals(response.getNotificationId(), deserialized.getNotificationId());
        assertEquals(response.getSuccess(), deserialized.getSuccess());
        assertEquals(response.getMessage(), deserialized.getMessage());
        assertEquals(response.getStatus(), deserialized.getStatus());
    }

    @Test
    void testJsonDeserialization_Sent() throws Exception {
        // Arrange
        String json = """
                {
                    "notificationId": 1,
                    "success": true,
                    "message": "Notification sent successfully",
                    "status": "SENT"
                }
                """;

        // Act
        NotificationResponse deserialized = objectMapper.readValue(json, NotificationResponse.class);

        // Assert
        assertEquals(1L, deserialized.getNotificationId());
        assertTrue(deserialized.getSuccess());
        assertEquals("Notification sent successfully", deserialized.getMessage());
        assertEquals("SENT", deserialized.getStatus());
    }

    @Test
    void testJsonDeserialization_Failed() throws Exception {
        // Arrange
        String json = """
                {
                    "notificationId": 2,
                    "success": false,
                    "message": "Failed to send notification",
                    "status": "FAILED"
                }
                """;

        // Act
        NotificationResponse deserialized = objectMapper.readValue(json, NotificationResponse.class);

        // Assert
        assertEquals(2L, deserialized.getNotificationId());
        assertFalse(deserialized.getSuccess());
        assertEquals("Failed to send notification", deserialized.getMessage());
        assertEquals("FAILED", deserialized.getStatus());
    }

    @Test
    void testJsonDeserialization_Pending() throws Exception {
        // Arrange
        String json = """
                {
                    "notificationId": 3,
                    "success": false,
                    "message": "Notification pending",
                    "status": "PENDING"
                }
                """;

        // Act
        NotificationResponse deserialized = objectMapper.readValue(json, NotificationResponse.class);

        // Assert
        assertEquals(3L, deserialized.getNotificationId());
        assertFalse(deserialized.getSuccess());
        assertEquals("Notification pending", deserialized.getMessage());
        assertEquals("PENDING", deserialized.getStatus());
    }

    @Test
    void testAllStatusValues() {
        // Test different status values
        response.setStatus("PENDING");
        assertEquals("PENDING", response.getStatus());

        response.setStatus("SENT");
        assertEquals("SENT", response.getStatus());

        response.setStatus("FAILED");
        assertEquals("FAILED", response.getStatus());
    }
}

