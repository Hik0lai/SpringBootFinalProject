package com.beehivemonitor.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for NotificationRequest DTO
 * Tests constructor, getters, setters, equals, hashCode, toString, and JSON serialization
 */
class NotificationRequestTest {

    private NotificationRequest request;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        request = new NotificationRequest();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testNoArgsConstructor() {
        // Act
        NotificationRequest emptyRequest = new NotificationRequest();

        // Assert
        assertNotNull(emptyRequest);
        assertNull(emptyRequest.getRecipientEmail());
        assertNull(request.getSubject());
        assertNull(request.getMessage());
        assertNull(request.getChannel());
        assertNull(request.getAlertId());
    }

    @Test
    void testAllArgsConstructor() {
        // Arrange
        String recipientEmail = "test@example.com";
        String subject = "Test Subject";
        String message = "Test Message";
        String channel = "EMAIL";
        UUID alertId = UUID.randomUUID();

        // Act
        NotificationRequest request = new NotificationRequest(recipientEmail, subject, message, channel, alertId);

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
        UUID alertId = UUID.randomUUID();

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
        UUID alertId = UUID.randomUUID();
        NotificationRequest request1 = new NotificationRequest(
                "test@example.com", "Subject", "Message", "EMAIL", alertId
        );
        NotificationRequest request2 = new NotificationRequest(
                "test@example.com", "Subject", "Message", "EMAIL", alertId
        );

        // Act & Assert
        assertEquals(request1, request2);
    }

    @Test
    void testEquals_NullAlertId() {
        // Arrange
        NotificationRequest request1 = new NotificationRequest(
                "test@example.com", "Subject", "Message", "EMAIL", null
        );
        NotificationRequest request2 = new NotificationRequest(
                "test@example.com", "Subject", "Message", "EMAIL", null
        );

        // Act & Assert
        assertEquals(request1, request2);
    }

    @Test
    void testHashCode_SameValues() {
        // Arrange
        UUID alertId = UUID.randomUUID();
        NotificationRequest request1 = new NotificationRequest(
                "test@example.com", "Subject", "Message", "EMAIL", alertId
        );
        NotificationRequest request2 = new NotificationRequest(
                "test@example.com", "Subject", "Message", "EMAIL", alertId
        );

        // Act & Assert
        assertEquals(request1.hashCode(), request2.hashCode());
    }

    @Test
    void testToString() {
        // Arrange
        UUID alertId = UUID.randomUUID();
        request.setRecipientEmail("test@example.com");
        request.setSubject("Test Subject");
        request.setMessage("Test Message");
        request.setChannel("EMAIL");
        request.setAlertId(alertId);

        // Act
        String toString = request.toString();

        // Assert
        assertNotNull(toString);
        assertTrue(toString.contains("test@example.com") || toString.contains("EMAIL"));
    }

    @Test
    void testJsonSerialization() throws Exception {
        // Arrange
        UUID alertId = UUID.randomUUID();
        request.setRecipientEmail("test@example.com");
        request.setSubject("Test Subject");
        request.setMessage("Test Message");
        request.setChannel("EMAIL");
        request.setAlertId(alertId);

        // Act
        String json = objectMapper.writeValueAsString(request);
        NotificationRequest deserialized = objectMapper.readValue(json, NotificationRequest.class);

        // Assert
        assertNotNull(json);
        assertEquals(request.getRecipientEmail(), deserialized.getRecipientEmail());
        assertEquals(request.getSubject(), deserialized.getSubject());
        assertEquals(request.getMessage(), deserialized.getMessage());
        assertEquals(request.getChannel(), deserialized.getChannel());
        assertEquals(request.getAlertId(), deserialized.getAlertId());
    }

    @Test
    void testJsonDeserialization_WithUUID() throws Exception {
        // Arrange
        UUID alertId = UUID.randomUUID();
        String json = String.format("""
                {
                    "recipientEmail": "test@example.com",
                    "subject": "Test Subject",
                    "message": "Test Message",
                    "channel": "EMAIL",
                    "alertId": "%s"
                }
                """, alertId);

        // Act
        NotificationRequest deserialized = objectMapper.readValue(json, NotificationRequest.class);

        // Assert
        assertEquals("test@example.com", deserialized.getRecipientEmail());
        assertEquals("Test Subject", deserialized.getSubject());
        assertEquals("Test Message", deserialized.getMessage());
        assertEquals("EMAIL", deserialized.getChannel());
        assertEquals(alertId, deserialized.getAlertId());
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
        NotificationRequest deserialized = objectMapper.readValue(json, NotificationRequest.class);

        // Assert
        assertEquals("test@example.com", deserialized.getRecipientEmail());
        assertNull(deserialized.getAlertId());
    }
}

