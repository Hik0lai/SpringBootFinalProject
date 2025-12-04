package com.beehivemonitor.notification.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for SendNotificationResponse DTO
 * Tests constructor, getters, setters, equals, hashCode, toString, and JSON serialization
 */
class SendNotificationResponseTest {

    private SendNotificationResponse response;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        response = new SendNotificationResponse();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testNoArgsConstructor() {
        // Act
        SendNotificationResponse emptyResponse = new SendNotificationResponse();

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
        SendNotificationResponse response = new SendNotificationResponse(
                notificationId, success, message, status
        );

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
        SendNotificationResponse response1 = new SendNotificationResponse(
                1L, true, "Success", "SENT"
        );
        SendNotificationResponse response2 = new SendNotificationResponse(
                1L, true, "Success", "SENT"
        );

        // Act & Assert
        assertEquals(response1, response2);
        assertEquals(response2, response1);
    }

    @Test
    void testEquals_DifferentValues() {
        // Arrange
        SendNotificationResponse response1 = new SendNotificationResponse(
                1L, true, "Success", "SENT"
        );
        SendNotificationResponse response2 = new SendNotificationResponse(
                2L, false, "Failed", "FAILED"
        );

        // Act & Assert
        assertNotEquals(response1, response2);
    }

    @Test
    void testEquals_NullValues() {
        // Arrange
        SendNotificationResponse response1 = new SendNotificationResponse(
                null, null, null, null
        );
        SendNotificationResponse response2 = new SendNotificationResponse(
                null, null, null, null
        );

        // Act & Assert
        assertEquals(response1, response2);
    }

    @Test
    void testHashCode_SameValues() {
        // Arrange
        SendNotificationResponse response1 = new SendNotificationResponse(
                1L, true, "Success", "SENT"
        );
        SendNotificationResponse response2 = new SendNotificationResponse(
                1L, true, "Success", "SENT"
        );

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
        assertTrue(toString.contains("1") || toString.contains("SENT"));
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
        SendNotificationResponse deserialized = objectMapper.readValue(json, SendNotificationResponse.class);

        // Assert
        assertNotNull(json);
        assertTrue(json.contains("SENT"));
        assertEquals(response.getNotificationId(), deserialized.getNotificationId());
        assertEquals(response.getSuccess(), deserialized.getSuccess());
        assertEquals(response.getMessage(), deserialized.getMessage());
        assertEquals(response.getStatus(), deserialized.getStatus());
    }

    @Test
    void testJsonDeserialization() throws Exception {
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
        SendNotificationResponse deserialized = objectMapper.readValue(json, SendNotificationResponse.class);

        // Assert
        assertEquals(1L, deserialized.getNotificationId());
        assertTrue(deserialized.getSuccess());
        assertEquals("Notification sent successfully", deserialized.getMessage());
        assertEquals("SENT", deserialized.getStatus());
    }

    @Test
    void testJsonDeserialization_FailedStatus() throws Exception {
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
        SendNotificationResponse deserialized = objectMapper.readValue(json, SendNotificationResponse.class);

        // Assert
        assertEquals(2L, deserialized.getNotificationId());
        assertFalse(deserialized.getSuccess());
        assertEquals("Failed to send notification", deserialized.getMessage());
        assertEquals("FAILED", deserialized.getStatus());
    }

    @Test
    void testJsonDeserialization_NullValues() throws Exception {
        // Arrange
        String json = """
                {
                    "notificationId": null,
                    "success": null,
                    "message": null,
                    "status": null
                }
                """;

        // Act
        SendNotificationResponse deserialized = objectMapper.readValue(json, SendNotificationResponse.class);

        // Assert
        assertNull(deserialized.getNotificationId());
        assertNull(deserialized.getSuccess());
        assertNull(deserialized.getMessage());
        assertNull(deserialized.getStatus());
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

