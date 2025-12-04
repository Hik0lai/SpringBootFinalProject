package com.beehivemonitor.security;

import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit Test for JwtTokenProvider
 * Tests JWT token generation and validation
 */
@ExtendWith(MockitoExtension.class)
class JwtTokenProviderTest {

    @InjectMocks
    private JwtTokenProvider jwtTokenProvider;

    private static final String TEST_SECRET = "testSecretKeyForBeehiveMonitorApplicationThatIsAtLeast256BitsLong123456789012345678901234567890";
    private static final long TEST_EXPIRATION = 86400000L; // 24 hours

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtSecret", TEST_SECRET);
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtExpirationMs", TEST_EXPIRATION);
    }

    @Test
    void testGenerateToken_Success() {
        // Arrange
        String email = "test@example.com";

        // Act
        String token = jwtTokenProvider.generateToken(email);

        // Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void testGetEmailFromToken_Success() {
        // Arrange
        String email = "test@example.com";
        String token = jwtTokenProvider.generateToken(email);

        // Act
        String extractedEmail = jwtTokenProvider.getEmailFromToken(token);

        // Assert
        assertEquals(email, extractedEmail);
    }

    @Test
    void testValidateToken_ValidToken_ReturnsTrue() {
        // Arrange
        String email = "test@example.com";
        String token = jwtTokenProvider.generateToken(email);

        // Act
        boolean isValid = jwtTokenProvider.validateToken(token);

        // Assert
        assertTrue(isValid);
    }

    @Test
    void testValidateToken_InvalidToken_ReturnsFalse() {
        // Arrange
        String invalidToken = "invalid.token.string";

        // Act
        boolean isValid = jwtTokenProvider.validateToken(invalidToken);

        // Assert
        assertFalse(isValid);
    }

    @Test
    void testValidateToken_EmptyToken_ReturnsFalse() {
        // Arrange
        String emptyToken = "";

        // Act
        boolean isValid = jwtTokenProvider.validateToken(emptyToken);

        // Assert
        assertFalse(isValid);
    }

    @Test
    void testValidateToken_NullToken_ReturnsFalse() {
        // Arrange
        String nullToken = null;

        // Act
        boolean isValid = jwtTokenProvider.validateToken(nullToken);

        // Assert
        assertFalse(isValid);
    }

    @Test
    void testGenerateAndValidateToken_DifferentEmails() {
        // Arrange
        String email1 = "user1@example.com";
        String email2 = "user2@example.com";

        // Act
        String token1 = jwtTokenProvider.generateToken(email1);
        String token2 = jwtTokenProvider.generateToken(email2);
        String extractedEmail1 = jwtTokenProvider.getEmailFromToken(token1);
        String extractedEmail2 = jwtTokenProvider.getEmailFromToken(token2);

        // Assert
        assertEquals(email1, extractedEmail1);
        assertEquals(email2, extractedEmail2);
        assertNotEquals(extractedEmail1, extractedEmail2);
    }
}

