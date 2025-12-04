package com.beehivemonitor.controller;

import com.beehivemonitor.entity.User;
import com.beehivemonitor.repository.UserRepository;
import com.beehivemonitor.security.CustomUserDetailsService;
import com.beehivemonitor.security.JwtAuthenticationFilter;
import com.beehivemonitor.security.JwtTokenProvider;
import com.beehivemonitor.service.SettingsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * API Test for SettingsController
 * Tests REST API endpoints using MockMvc
 */
@WebMvcTest(controllers = SettingsController.class, excludeAutoConfiguration = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
        org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration.class
})
@AutoConfigureMockMvc(addFilters = false)
class SettingsControllerApiTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SettingsService settingsService;

    @MockBean
    private JwtTokenProvider tokenProvider;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private String validToken;
    private String testEmail;
    private User testUser;

    @BeforeEach
    void setUp() {
        validToken = "Bearer valid-jwt-token";
        testEmail = "test@example.com";

        testUser = new User();
        testUser.setId(UUID.randomUUID());
        testUser.setEmail(testEmail);
        testUser.setRole(User.Role.USER);

        // Mock JWT token provider
        when(tokenProvider.getEmailFromToken(anyString())).thenReturn(testEmail);
        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(testUser));
    }

    @Test
    void testGetSettings_Success() throws Exception {
        // Arrange
        com.beehivemonitor.entity.UserSettings userSettings = new com.beehivemonitor.entity.UserSettings();
        userSettings.setMeasurementIntervalMinutes(5);
        userSettings.setUser(testUser);
        
        when(settingsService.getSettingsByEmail(testEmail))
                .thenReturn(userSettings);

        // Act & Assert
        mockMvc.perform(get("/api/settings")
                .header("Authorization", validToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.measurementIntervalMinutes").value(5));
    }

    @Test
    void testGetSettings_DefaultInterval_Success() throws Exception {
        // Arrange
        com.beehivemonitor.entity.UserSettings userSettings = new com.beehivemonitor.entity.UserSettings();
        userSettings.setMeasurementIntervalMinutes(1);
        userSettings.setUser(testUser);
        
        when(settingsService.getSettingsByEmail(testEmail))
                .thenReturn(userSettings);

        // Act & Assert
        mockMvc.perform(get("/api/settings")
                .header("Authorization", validToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.measurementIntervalMinutes").value(1));
    }

    @Test
    void testUpdateSettings_Success() throws Exception {
        // Arrange
        SettingsController.SettingsRequest request = new SettingsController.SettingsRequest();
        request.measurementIntervalMinutes = 10;

        // Act & Assert
        mockMvc.perform(put("/api/settings")
                .header("Authorization", validToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Change is saved"));
    }

    @Test
    void testUpdateSettings_CustomInterval_Success() throws Exception {
        // Arrange
        SettingsController.SettingsRequest request = new SettingsController.SettingsRequest();
        request.measurementIntervalMinutes = 30;

        // Act & Assert
        mockMvc.perform(put("/api/settings")
                .header("Authorization", validToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Change is saved"));
    }

    @Test
    void testUpdateSettings_InvalidJson_ReturnsBadRequest() throws Exception {
        // Arrange
        String invalidJson = "{ invalid json }";

        // Act & Assert
        mockMvc.perform(put("/api/settings")
                .header("Authorization", validToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetSettings_MissingToken_ReturnsBadRequest() throws Exception {
        // Act & Assert
        // Spring returns 400 Bad Request when required header is missing
        mockMvc.perform(get("/api/settings"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateSettings_MissingToken_ReturnsBadRequest() throws Exception {
        // Arrange
        SettingsController.SettingsRequest request = new SettingsController.SettingsRequest();
        request.measurementIntervalMinutes = 5;

        // Act & Assert
        // Spring returns 400 Bad Request when required header is missing
        mockMvc.perform(put("/api/settings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateSettings_NullInterval_Success() throws Exception {
        // Arrange - null interval might be handled by the service
        SettingsController.SettingsRequest request = new SettingsController.SettingsRequest();
        request.measurementIntervalMinutes = null;

        // Act & Assert - The request will be processed, validation happens in service
        mockMvc.perform(put("/api/settings")
                .header("Authorization", validToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk()); // Service validates
    }
}

