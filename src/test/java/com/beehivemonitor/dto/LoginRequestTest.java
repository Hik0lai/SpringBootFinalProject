package com.beehivemonitor.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for LoginRequest DTO
 * Tests constructor, getters, setters, equals, hashCode, toString, JSON serialization, and validation
 */
class LoginRequestTest {

    private LoginRequest request;
    private ObjectMapper objectMapper;
    private Validator validator;

    @BeforeEach
    void setUp() {
        request = new LoginRequest();
        objectMapper = new ObjectMapper();
        
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testNoArgsConstructor() {
        // Act
        LoginRequest emptyRequest = new LoginRequest();

        // Assert
        assertNotNull(emptyRequest);
        assertNull(emptyRequest.getEmail());
        assertNull(emptyRequest.getPassword());
    }

    @Test
    void testAllArgsConstructor() {
        // Arrange
        String email = "test@example.com";
        String password = "password123";

        // Act
        LoginRequest request = new LoginRequest(email, password);

        // Assert
        assertEquals(email, request.getEmail());
        assertEquals(password, request.getPassword());
    }

    @Test
    void testGettersAndSetters() {
        // Arrange
        String email = "test@example.com";
        String password = "password123";

        // Act
        request.setEmail(email);
        request.setPassword(password);

        // Assert
        assertEquals(email, request.getEmail());
        assertEquals(password, request.getPassword());
    }

    @Test
    void testEquals_SameValues() {
        // Arrange
        LoginRequest request1 = new LoginRequest("test@example.com", "password123");
        LoginRequest request2 = new LoginRequest("test@example.com", "password123");

        // Act & Assert
        assertEquals(request1, request2);
        assertEquals(request2, request1);
    }

    @Test
    void testEquals_DifferentValues() {
        // Arrange
        LoginRequest request1 = new LoginRequest("test@example.com", "password123");
        LoginRequest request2 = new LoginRequest("other@example.com", "password123");

        // Act & Assert
        assertNotEquals(request1, request2);
    }

    @Test
    void testHashCode_SameValues() {
        // Arrange
        LoginRequest request1 = new LoginRequest("test@example.com", "password123");
        LoginRequest request2 = new LoginRequest("test@example.com", "password123");

        // Act & Assert
        assertEquals(request1.hashCode(), request2.hashCode());
    }

    @Test
    void testToString() {
        // Arrange
        request.setEmail("test@example.com");
        request.setPassword("password123");

        // Act
        String toString = request.toString();

        // Assert
        assertNotNull(toString);
        // Password should not appear in toString for security
        assertFalse(toString.contains("password123"));
    }

    @Test
    void testJsonSerialization() throws Exception {
        // Arrange
        request.setEmail("test@example.com");
        request.setPassword("password123");

        // Act
        String json = objectMapper.writeValueAsString(request);
        LoginRequest deserialized = objectMapper.readValue(json, LoginRequest.class);

        // Assert
        assertNotNull(json);
        assertTrue(json.contains("test@example.com"));
        assertEquals(request.getEmail(), deserialized.getEmail());
        assertEquals(request.getPassword(), deserialized.getPassword());
    }

    @Test
    void testJsonDeserialization() throws Exception {
        // Arrange
        String json = """
                {
                    "email": "test@example.com",
                    "password": "password123"
                }
                """;

        // Act
        LoginRequest deserialized = objectMapper.readValue(json, LoginRequest.class);

        // Assert
        assertEquals("test@example.com", deserialized.getEmail());
        assertEquals("password123", deserialized.getPassword());
    }

    @Test
    void testValidation_ValidEmail() {
        // Arrange
        request.setEmail("test@example.com");
        request.setPassword("password123");

        // Act
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

        // Assert
        assertTrue(violations.isEmpty());
    }

    @Test
    void testValidation_InvalidEmail() {
        // Arrange
        request.setEmail("invalid-email");
        request.setPassword("password123");

        // Act
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")));
    }

    @Test
    void testValidation_BlankEmail() {
        // Arrange
        request.setEmail("");
        request.setPassword("password123");

        // Act
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")));
    }

    @Test
    void testValidation_NullEmail() {
        // Arrange
        request.setEmail(null);
        request.setPassword("password123");

        // Act
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")));
    }

    @Test
    void testValidation_BlankPassword() {
        // Arrange
        request.setEmail("test@example.com");
        request.setPassword("");

        // Act
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("password")));
    }

    @Test
    void testValidation_NullPassword() {
        // Arrange
        request.setEmail("test@example.com");
        request.setPassword(null);

        // Act
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("password")));
    }
}

