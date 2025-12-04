package com.beehivemonitor.notification.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for SendNotificationRequest DTO
 * Tests constructor, getters, setters, equals, hashCode, toString, and JSON serialization
 */
class SendNotificationRequestTest {

    private SendNotificationRequest request;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        request = new SendNotificationRequest();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testNoArgsConstructor() {
        // Act
        SendNotificationRequest emptyRequest = new SendNotificationRequest();

        // Assert
        assertNotNull(emptyRequest);
        assertNull(emptyRequest.getRecipientEmail());
        assertNull(emptyRequest.getSubject());
        assertNull(emptyRequest.getMessage());
        assertNull(emptyRequest.getChannel());
        assertNull(emptyRequest.getAlertId());
    }

    @Test
    void testAllArgsConstructor() {
        // Arrange
        String recipientEmail = "test@example.com";
        String subject = "Test Subject";
        String message = "Test Message";
        String channel = "EMAIL";
        Long alertId = 1L;

        // Act
        SendNotificationRequest request = new SendNotificationRequest(
                recipientEmail, subject, message, channel, alertId
        );

        // Assert
        assertEquals(recipientEmail, request.getRecipientEmail());
        assertEquals(subject, request.getSubject());
        assertEquals(message, request.getMessage());
        assertEquals(channel, request.getChannel());
        assertEquals(alertId, request.getAlertId());
    }

    @Test
    void testGettersAndSetters() {
        // Arrange
        String recipientEmail = "test@example.com";
        String subject = "Test Subject";
        String message = "Test Message";
        String channel = "EMAIL";
        Long alertId = 1L;

        // Act
        request.setRecipientEmail(recipientEmail);
        request.setSubject(subject);
        request.setMessage(message);
        request.setChannel(channel);
        request.setAlertId(alertId);

        // Assert
        assertEquals(recipientEmail, request.getRecipientEmail());
        assertEquals(subject, request.getSubject());
        assertEquals(message, request.getMessage());
        assertEquals(channel, request.getChannel());
        assertEquals(alertId, request.getAlertId());
    }

    @Test
    void testEquals_SameValues() {
        // Arrange
        SendNotificationRequest request1 = new SendNotificationRequest(
                "test@example.com", "Subject", "Message", "EMAIL", 1L
        );
        SendNotificationRequest request2 = new SendNotificationRequest(
                "test@example.com", "Subject", "Message", "EMAIL", 1L
        );

        // Act & Assert
        assertEquals(request1, request2);
        assertEquals(request2, request1);
    }

    @Test
    void testEquals_DifferentValues() {
        // Arrange
        SendNotificationRequest request1 = new SendNotificationRequest(
                "test@example.com", "Subject", "Message", "EMAIL", 1L
        );
        SendNotificationRequest request2 = new SendNotificationRequest(
                "other@example.com", "Subject", "Message", "EMAIL", 1L
        );

        // Act & Assert
        assertNotEquals(request1, request2);
    }

    @Test
    void testEquals_NullAlertId() {
        // Arrange
        SendNotificationRequest request1 = new SendNotificationRequest(
                "test@example.com", "Subject", "Message", "EMAIL", null
        );
        SendNotificationRequest request2 = new SendNotificationRequest(
                "test@example.com", "Subject", "Message", "EMAIL", null
        );

        // Act & Assert
        assertEquals(request1, request2);
    }

    @Test
    void testHashCode_SameValues() {
        // Arrange
        SendNotificationRequest request1 = new SendNotificationRequest(
                "test@example.com", "Subject", "Message", "EMAIL", 1L
        );
        SendNotificationRequest request2 = new SendNotificationRequest(
                "test@example.com", "Subject", "Message", "EMAIL", 1L
        );

        // Act & Assert
        assertEquals(request1.hashCode(), request2.hashCode());
    }

    @Test
    void testToString() {
        // Arrange
        request.setRecipientEmail("test@example.com");
        request.setSubject("Test Subject");
        request.setMessage("Test Message");
        request.setChannel("EMAIL");
        request.setAlertId(1L);

        // Act
        String toString = request.toString();

        // Assert
        assertNotNull(toString);
        assertTrue(toString.contains("test@example.com"));
        assertTrue(toString.contains("Test Subject"));
        assertTrue(toString.contains("EMAIL"));
    }

    @Test
    void testJsonSerialization() throws Exception {
        // Arrange
        request.setRecipientEmail("test@example.com");
        request.setSubject("Test Subject");
        request.setMessage("Test Message");
        request.setChannel("EMAIL");
        request.setAlertId(1L);

        // Act
        String json = objectMapper.writeValueAsString(request);
        SendNotificationRequest deserialized = objectMapper.readValue(json, SendNotificationRequest.class);

        // Assert
        assertNotNull(json);
        assertTrue(json.contains("test@example.com"));
        assertEquals(request.getRecipientEmail(), deserialized.getRecipientEmail());
        assertEquals(request.getSubject(), deserialized.getSubject());
        assertEquals(request.getMessage(), deserialized.getMessage());
        assertEquals(request.getChannel(), deserialized.getChannel());
        assertEquals(request.getAlertId(), deserialized.getAlertId());
    }

    @Test
    void testJsonDeserialization() throws Exception {
        // Arrange
        String json = """
                {
                    "recipientEmail": "test@example.com",
                    "subject": "Test Subject",
                    "message": "Test Message",
                    "channel": "EMAIL",
                    "alertId": 1
                }
                """;

        // Act
        SendNotificationRequest deserialized = objectMapper.readValue(json, SendNotificationRequest.class);

        // Assert
        assertEquals("test@example.com", deserialized.getRecipientEmail());
        assertEquals("Test Subject", deserialized.getSubject());
        assertEquals("Test Message", deserialized.getMessage());
        assertEquals("EMAIL", deserialized.getChannel());
        assertEquals(1L, deserialized.getAlertId());
    }

    @Test
    void testJsonDeserialization_NullAlertId() throws Exception {
        // Arrange
        String json = """
                {
                    "recipientEmail": "test@example.com",
                    "subject": "Test Subject",
                    "message": "Test Message",
                    "channel": "EMAIL",
                    "alertId": null
                }
                """;

        // Act
        SendNotificationRequest deserialized = objectMapper.readValue(json, SendNotificationRequest.class);

        // Assert
        assertEquals("test@example.com", deserialized.getRecipientEmail());
        assertNull(deserialized.getAlertId());
    }
}

