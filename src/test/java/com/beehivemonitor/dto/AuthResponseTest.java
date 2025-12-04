package com.beehivemonitor.dto;

import com.beehivemonitor.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for AuthResponse DTO and its nested UserResponse class
 * Tests constructor, getters, setters, equals, hashCode, toString, JSON serialization, and fromUser method
 */
class AuthResponseTest {

    private AuthResponse authResponse;
    private AuthResponse.UserResponse userResponse;
    private ObjectMapper objectMapper;
    private User testUser;

    @BeforeEach
    void setUp() {
        authResponse = new AuthResponse();
        objectMapper = new ObjectMapper();
        
        UUID userId = UUID.randomUUID();
        testUser = new User();
        testUser.setId(userId);
        testUser.setName("Test User");
        testUser.setEmail("test@example.com");
        testUser.setRole(User.Role.USER);
        testUser.setEmailNotificationEnabled(true);
        testUser.setTelephone("1234567890");
    }

    // AuthResponse Tests

    @Test
    void testNoArgsConstructor() {
        // Act
        AuthResponse emptyResponse = new AuthResponse();

        // Assert
        assertNotNull(emptyResponse);
        assertNull(emptyResponse.getToken());
        assertNull(emptyResponse.getUser());
    }

    @Test
    void testAllArgsConstructor() {
        // Arrange
        String token = "jwt-token-123";
        AuthResponse.UserResponse user = AuthResponse.UserResponse.fromUser(testUser);

        // Act
        AuthResponse response = new AuthResponse(token, user);

        // Assert
        assertEquals(token, response.getToken());
        assertEquals(user, response.getUser());
    }

    @Test
    void testGettersAndSetters() {
        // Arrange
        String token = "jwt-token-123";
        AuthResponse.UserResponse user = AuthResponse.UserResponse.fromUser(testUser);

        // Act
        authResponse.setToken(token);
        authResponse.setUser(user);

        // Assert
        assertEquals(token, authResponse.getToken());
        assertEquals(user, authResponse.getUser());
    }

    @Test
    void testEquals_SameValues() {
        // Arrange
        String token = "jwt-token-123";
        AuthResponse.UserResponse user = AuthResponse.UserResponse.fromUser(testUser);
        AuthResponse response1 = new AuthResponse(token, user);
        AuthResponse response2 = new AuthResponse(token, user);

        // Act & Assert
        assertEquals(response1, response2);
    }

    @Test
    void testToString() {
        // Arrange
        authResponse.setToken("jwt-token-123");
        authResponse.setUser(AuthResponse.UserResponse.fromUser(testUser));

        // Act
        String toString = authResponse.toString();

        // Assert
        assertNotNull(toString);
    }

    @Test
    void testJsonSerialization() throws Exception {
        // Arrange
        authResponse.setToken("jwt-token-123");
        authResponse.setUser(AuthResponse.UserResponse.fromUser(testUser));

        // Act
        String json = objectMapper.writeValueAsString(authResponse);
        AuthResponse deserialized = objectMapper.readValue(json, AuthResponse.class);

        // Assert
        assertNotNull(json);
        assertEquals(authResponse.getToken(), deserialized.getToken());
        assertNotNull(deserialized.getUser());
    }

    // UserResponse Tests

    @Test
    void testUserResponse_NoArgsConstructor() {
        // Act
        AuthResponse.UserResponse emptyUser = new AuthResponse.UserResponse();

        // Assert
        assertNotNull(emptyUser);
        assertNull(emptyUser.getId());
        assertNull(emptyUser.getName());
        assertNull(emptyUser.getEmail());
        assertNull(emptyUser.getRole());
        assertNull(emptyUser.getEmailNotificationEnabled());
        assertNull(emptyUser.getTelephone());
    }

    @Test
    void testUserResponse_AllArgsConstructor() {
        // Arrange
        UUID id = UUID.randomUUID();
        String name = "Test User";
        String email = "test@example.com";
        User.Role role = User.Role.USER;
        Boolean emailNotificationEnabled = true;
        String telephone = "1234567890";

        // Act
        AuthResponse.UserResponse user = new AuthResponse.UserResponse(
                id, name, email, role, emailNotificationEnabled, telephone
        );

        // Assert
        assertEquals(id, user.getId());
        assertEquals(name, user.getName());
        assertEquals(email, user.getEmail());
        assertEquals(role, user.getRole());
        assertEquals(emailNotificationEnabled, user.getEmailNotificationEnabled());
        assertEquals(telephone, user.getTelephone());
    }

    @Test
    void testUserResponse_FromUser() {
        // Act
        AuthResponse.UserResponse userResponse = AuthResponse.UserResponse.fromUser(testUser);

        // Assert
        assertNotNull(userResponse);
        assertEquals(testUser.getId(), userResponse.getId());
        assertEquals(testUser.getName(), userResponse.getName());
        assertEquals(testUser.getEmail(), userResponse.getEmail());
        assertEquals(testUser.getRole(), userResponse.getRole());
        assertEquals(testUser.getEmailNotificationEnabled(), userResponse.getEmailNotificationEnabled());
        assertEquals(testUser.getTelephone(), userResponse.getTelephone());
    }

    @Test
    void testUserResponse_FromUser_WithNullEmailNotificationEnabled() {
        // Arrange
        testUser.setEmailNotificationEnabled(null);

        // Act
        AuthResponse.UserResponse userResponse = AuthResponse.UserResponse.fromUser(testUser);

        // Assert
        assertNotNull(userResponse);
        assertFalse(userResponse.getEmailNotificationEnabled()); // Should default to false
    }

    @Test
    void testUserResponse_FromUser_WithNullTelephone() {
        // Arrange
        testUser.setTelephone(null);

        // Act
        AuthResponse.UserResponse userResponse = AuthResponse.UserResponse.fromUser(testUser);

        // Assert
        assertNotNull(userResponse);
        assertNull(userResponse.getTelephone());
    }

    @Test
    void testUserResponse_Equals() {
        // Arrange
        UUID id = UUID.randomUUID();
        AuthResponse.UserResponse user1 = new AuthResponse.UserResponse(
                id, "Test User", "test@example.com", User.Role.USER, true, "1234567890"
        );
        AuthResponse.UserResponse user2 = new AuthResponse.UserResponse(
                id, "Test User", "test@example.com", User.Role.USER, true, "1234567890"
        );

        // Act & Assert
        assertEquals(user1, user2);
    }

    @Test
    void testUserResponse_HashCode() {
        // Arrange
        UUID id = UUID.randomUUID();
        AuthResponse.UserResponse user1 = new AuthResponse.UserResponse(
                id, "Test User", "test@example.com", User.Role.USER, true, "1234567890"
        );
        AuthResponse.UserResponse user2 = new AuthResponse.UserResponse(
                id, "Test User", "test@example.com", User.Role.USER, true, "1234567890"
        );

        // Act & Assert
        assertEquals(user1.hashCode(), user2.hashCode());
    }

    @Test
    void testUserResponse_ToString() {
        // Arrange
        AuthResponse.UserResponse user = AuthResponse.UserResponse.fromUser(testUser);

        // Act
        String toString = user.toString();

        // Assert
        assertNotNull(toString);
    }

    @Test
    void testJsonSerialization_UserResponse() throws Exception {
        // Arrange
        AuthResponse.UserResponse user = AuthResponse.UserResponse.fromUser(testUser);

        // Act
        String json = objectMapper.writeValueAsString(user);
        AuthResponse.UserResponse deserialized = objectMapper.readValue(json, AuthResponse.UserResponse.class);

        // Assert
        assertNotNull(json);
        assertEquals(user.getId(), deserialized.getId());
        assertEquals(user.getName(), deserialized.getName());
        assertEquals(user.getEmail(), deserialized.getEmail());
        assertEquals(user.getRole(), deserialized.getRole());
    }

    @Test
    void testJsonDeserialization_FullAuthResponse() throws Exception {
        // Arrange
        String json = """
                {
                    "token": "jwt-token-123",
                    "user": {
                        "id": "550e8400-e29b-41d4-a716-446655440000",
                        "name": "Test User",
                        "email": "test@example.com",
                        "role": "USER",
                        "emailNotificationEnabled": true,
                        "telephone": "1234567890"
                    }
                }
                """;

        // Act
        AuthResponse deserialized = objectMapper.readValue(json, AuthResponse.class);

        // Assert
        assertEquals("jwt-token-123", deserialized.getToken());
        assertNotNull(deserialized.getUser());
        assertEquals("Test User", deserialized.getUser().getName());
        assertEquals("test@example.com", deserialized.getUser().getEmail());
        assertEquals(User.Role.USER, deserialized.getUser().getRole());
    }
}

