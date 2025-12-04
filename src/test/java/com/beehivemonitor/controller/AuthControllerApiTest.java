package com.beehivemonitor.controller;

import com.beehivemonitor.dto.AuthResponse;
import com.beehivemonitor.dto.LoginRequest;
import com.beehivemonitor.dto.RegisterRequest;
import com.beehivemonitor.entity.User;
import com.beehivemonitor.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * API Test for AuthController
 * Tests the REST API endpoints using MockMvc
 * Auth endpoints are public (permitAll), so security is automatically bypassed
 * 
 * NOTE: Currently disabled due to SecurityConfig loading issues in @WebMvcTest
 * TODO: Fix security configuration exclusion for this test
 */
@Disabled("SecurityConfig loading issue - needs fix")
@WebMvcTest(controllers = AuthController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.REGEX, pattern = "com\\.beehivemonitor\\.security\\..*"))
class AuthControllerApiTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testRegisterEndpoint_Success() throws Exception {
        // Arrange
        RegisterRequest request = new RegisterRequest();
        request.setName("Test User");
        request.setEmail("test@example.com");
        request.setPassword("password123");
        request.setTelephone("1234567890");

        User user = new User();
        user.setId(UUID.randomUUID());
        user.setName("Test User");
        user.setEmail("test@example.com");
        user.setRole(User.Role.USER);
        user.setEmailNotificationEnabled(false);
        user.setTelephone("1234567890");

        AuthResponse.UserResponse userResponse = AuthResponse.UserResponse.fromUser(user);
        AuthResponse response = new AuthResponse("test-jwt-token", userResponse);

        when(authService.register(any(RegisterRequest.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.token").value("test-jwt-token"))
                .andExpect(jsonPath("$.user.email").value("test@example.com"))
                .andExpect(jsonPath("$.user.name").value("Test User"))
                .andExpect(jsonPath("$.user.role").value("USER"));
    }

    @Test
    void testRegisterEndpoint_InvalidEmail_ReturnsBadRequest() throws Exception {
        // Arrange
        RegisterRequest request = new RegisterRequest();
        request.setName("Test User");
        request.setEmail("invalid-email"); // Invalid email format
        request.setPassword("password123");

        // Act & Assert - Validation should fail
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testRegisterEndpoint_MissingFields_ReturnsBadRequest() throws Exception {
        // Arrange
        RegisterRequest request = new RegisterRequest();
        // Missing required fields

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testLoginEndpoint_Success() throws Exception {
        // Arrange
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("password123");

        User user = new User();
        user.setId(UUID.randomUUID());
        user.setName("Test User");
        user.setEmail("test@example.com");
        user.setRole(User.Role.USER);
        user.setEmailNotificationEnabled(false);

        AuthResponse.UserResponse userResponse = AuthResponse.UserResponse.fromUser(user);
        AuthResponse response = new AuthResponse("test-jwt-token", userResponse);

        when(authService.login(any(LoginRequest.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.token").value("test-jwt-token"))
                .andExpect(jsonPath("$.user.email").value("test@example.com"))
                .andExpect(jsonPath("$.user.name").value("Test User"));
    }

    @Test
    void testLoginEndpoint_InvalidEmail_ReturnsBadRequest() throws Exception {
        // Arrange
        LoginRequest request = new LoginRequest();
        request.setEmail("invalid-email"); // Invalid email format
        request.setPassword("password123");

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testLoginEndpoint_MissingPassword_ReturnsBadRequest() throws Exception {
        // Arrange
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        // Missing password

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testRegisterEndpoint_OptionalTelephone() throws Exception {
        // Arrange
        RegisterRequest request = new RegisterRequest();
        request.setName("Test User");
        request.setEmail("test@example.com");
        request.setPassword("password123");
        // Telephone is optional, so it can be null

        User user = new User();
        user.setId(UUID.randomUUID());
        user.setName("Test User");
        user.setEmail("test@example.com");
        user.setRole(User.Role.USER);
        user.setEmailNotificationEnabled(false);

        AuthResponse.UserResponse userResponse = AuthResponse.UserResponse.fromUser(user);
        AuthResponse response = new AuthResponse("test-jwt-token", userResponse);

        when(authService.register(any(RegisterRequest.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.user.email").value("test@example.com"));
    }
}

