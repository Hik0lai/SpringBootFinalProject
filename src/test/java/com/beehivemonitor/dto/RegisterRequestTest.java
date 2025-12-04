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
 * Test for RegisterRequest DTO
 * Tests constructor, getters, setters, equals, hashCode, toString, JSON serialization, and validation
 */
class RegisterRequestTest {

    private RegisterRequest request;
    private ObjectMapper objectMapper;
    private Validator validator;

    @BeforeEach
    void setUp() {
        request = new RegisterRequest();
        objectMapper = new ObjectMapper();
        
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testNoArgsConstructor() {
        // Act
        RegisterRequest emptyRequest = new RegisterRequest();

        // Assert
        assertNotNull(emptyRequest);
        assertNull(emptyRequest.getName());
        assertNull(emptyRequest.getEmail());
        assertNull(emptyRequest.getPassword());
        assertNull(emptyRequest.getTelephone());
    }

    @Test
    void testAllArgsConstructor() {
        // Arrange
        String name = "Test User";
        String email = "test@example.com";
        String password = "password123";
        String telephone = "1234567890";

        // Act
        RegisterRequest request = new RegisterRequest(name, email, password, telephone);

        // Assert
        assertEquals(name, request.getName());
        assertEquals(email, request.getEmail());
        assertEquals(password, request.getPassword());
        assertEquals(telephone, request.getTelephone());
    }

    @Test
    void testGettersAndSetters() {
        // Arrange
        String name = "Test User";
        String email = "test@example.com";
        String password = "password123";
        String telephone = "1234567890";

        // Act
        request.setName(name);
        request.setEmail(email);
        request.setPassword(password);
        request.setTelephone(telephone);

        // Assert
        assertEquals(name, request.getName());
        assertEquals(email, request.getEmail());
        assertEquals(password, request.getPassword());
        assertEquals(telephone, request.getTelephone());
    }

    @Test
    void testEquals_SameValues() {
        // Arrange
        RegisterRequest request1 = new RegisterRequest("Test User", "test@example.com", "password123", "1234567890");
        RegisterRequest request2 = new RegisterRequest("Test User", "test@example.com", "password123", "1234567890");

        // Act & Assert
        assertEquals(request1, request2);
        assertEquals(request2, request1);
    }

    @Test
    void testEquals_DifferentValues() {
        // Arrange
        RegisterRequest request1 = new RegisterRequest("Test User", "test@example.com", "password123", "1234567890");
        RegisterRequest request2 = new RegisterRequest("Other User", "test@example.com", "password123", "1234567890");

        // Act & Assert
        assertNotEquals(request1, request2);
    }

    @Test
    void testEquals_NullTelephone() {
        // Arrange
        RegisterRequest request1 = new RegisterRequest("Test User", "test@example.com", "password123", null);
        RegisterRequest request2 = new RegisterRequest("Test User", "test@example.com", "password123", null);

        // Act & Assert
        assertEquals(request1, request2);
    }

    @Test
    void testHashCode_SameValues() {
        // Arrange
        RegisterRequest request1 = new RegisterRequest("Test User", "test@example.com", "password123", "1234567890");
        RegisterRequest request2 = new RegisterRequest("Test User", "test@example.com", "password123", "1234567890");

        // Act & Assert
        assertEquals(request1.hashCode(), request2.hashCode());
    }

    @Test
    void testToString() {
        // Arrange
        request.setName("Test User");
        request.setEmail("test@example.com");
        request.setPassword("password123");
        request.setTelephone("1234567890");

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
        request.setName("Test User");
        request.setEmail("test@example.com");
        request.setPassword("password123");
        request.setTelephone("1234567890");

        // Act
        String json = objectMapper.writeValueAsString(request);
        RegisterRequest deserialized = objectMapper.readValue(json, RegisterRequest.class);

        // Assert
        assertNotNull(json);
        assertTrue(json.contains("Test User"));
        assertTrue(json.contains("test@example.com"));
        assertEquals(request.getName(), deserialized.getName());
        assertEquals(request.getEmail(), deserialized.getEmail());
        assertEquals(request.getPassword(), deserialized.getPassword());
        assertEquals(request.getTelephone(), deserialized.getTelephone());
    }

    @Test
    void testJsonDeserialization() throws Exception {
        // Arrange
        String json = """
                {
                    "name": "Test User",
                    "email": "test@example.com",
                    "password": "password123",
                    "telephone": "1234567890"
                }
                """;

        // Act
        RegisterRequest deserialized = objectMapper.readValue(json, RegisterRequest.class);

        // Assert
        assertEquals("Test User", deserialized.getName());
        assertEquals("test@example.com", deserialized.getEmail());
        assertEquals("password123", deserialized.getPassword());
        assertEquals("1234567890", deserialized.getTelephone());
    }

    @Test
    void testJsonDeserialization_WithoutTelephone() throws Exception {
        // Arrange
        String json = """
                {
                    "name": "Test User",
                    "email": "test@example.com",
                    "password": "password123"
                }
                """;

        // Act
        RegisterRequest deserialized = objectMapper.readValue(json, RegisterRequest.class);

        // Assert
        assertEquals("Test User", deserialized.getName());
        assertEquals("test@example.com", deserialized.getEmail());
        assertEquals("password123", deserialized.getPassword());
        assertNull(deserialized.getTelephone());
    }

    @Test
    void testValidation_ValidRequest() {
        // Arrange
        request.setName("Test User");
        request.setEmail("test@example.com");
        request.setPassword("password123");
        request.setTelephone("1234567890");

        // Act
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

        // Assert
        assertTrue(violations.isEmpty());
    }

    @Test
    void testValidation_BlankName() {
        // Arrange
        request.setName("");
        request.setEmail("test@example.com");
        request.setPassword("password123");

        // Act
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("name")));
    }

    @Test
    void testValidation_InvalidEmail() {
        // Arrange
        request.setName("Test User");
        request.setEmail("invalid-email");
        request.setPassword("password123");

        // Act
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")));
    }

    @Test
    void testValidation_BlankPassword() {
        // Arrange
        request.setName("Test User");
        request.setEmail("test@example.com");
        request.setPassword("");

        // Act
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("password")));
    }

    @Test
    void testValidation_OptionalTelephone() {
        // Arrange
        request.setName("Test User");
        request.setEmail("test@example.com");
        request.setPassword("password123");
        request.setTelephone(null); // Optional field

        // Act
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

        // Assert
        assertTrue(violations.isEmpty()); // Telephone is optional, no validation errors
    }
}

