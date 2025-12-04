package com.beehivemonitor.controller;

import com.beehivemonitor.entity.Alert;
import com.beehivemonitor.entity.Hive;
import com.beehivemonitor.entity.User;
import com.beehivemonitor.security.CustomUserDetailsService;
import com.beehivemonitor.security.JwtAuthenticationFilter;
import com.beehivemonitor.security.JwtTokenProvider;
import com.beehivemonitor.service.AlertService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * API Test for AlertController
 * Tests REST API endpoints using MockMvc
 */
@WebMvcTest(controllers = AlertController.class, excludeAutoConfiguration = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
        org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration.class
})
@AutoConfigureMockMvc(addFilters = false)
class AlertControllerApiTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AlertService alertService;

    @MockBean
    private JwtTokenProvider tokenProvider;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private ObjectMapper objectMapper;

    private String validToken;
    private String testEmail;
    private User testUser;
    private Hive testHive;
    private Alert testAlert;

    @BeforeEach
    void setUp() {
        validToken = "Bearer valid-jwt-token";
        testEmail = "test@example.com";
        
        testUser = new User();
        testUser.setId(UUID.randomUUID());
        testUser.setEmail(testEmail);
        testUser.setRole(User.Role.USER);
        
        testHive = new Hive();
        testHive.setId(UUID.randomUUID());
        testHive.setName("Test Hive");
        testHive.setUser(testUser);
        
        testAlert = new Alert();
        testAlert.setId(UUID.randomUUID());
        testAlert.setName("Test Alert");
        testAlert.setHive(testHive);
        testAlert.setTriggerConditions("[{\"parameter\":\"temperature\",\"operator\":\">\",\"value\":30.0}]");
        testAlert.setIsTriggered(false);
        testAlert.setCreatedAt(LocalDateTime.now());

        // Mock JWT token provider
        when(tokenProvider.getEmailFromToken(anyString())).thenReturn(testEmail);
    }

    @Test
    void testGetAllAlerts_Success() throws Exception {
        // Arrange
        List<Alert> alerts = Arrays.asList(testAlert);
        when(alertService.getAllAlertsByUser(testEmail)).thenReturn(alerts);

        // Act & Assert
        mockMvc.perform(get("/api/alerts")
                .header("Authorization", validToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].name").value("Test Alert"))
                .andExpect(jsonPath("$[0].hiveName").value("Test Hive"));
    }

    @Test
    void testGetAlertById_Success() throws Exception {
        // Arrange
        when(alertService.getAlertById(testAlert.getId(), testEmail)).thenReturn(testAlert);

        // Act & Assert
        mockMvc.perform(get("/api/alerts/{id}", testAlert.getId())
                .header("Authorization", validToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(testAlert.getId().toString()))
                .andExpect(jsonPath("$.name").value("Test Alert"));
    }

    @Test
    void testCreateAlert_Success() throws Exception {
        // Arrange
        AlertController.AlertRequest request = new AlertController.AlertRequest();
        request.name = "New Alert";
        request.hiveId = testHive.getId();
        request.triggerConditions = "[{\"parameter\":\"temperature\",\"operator\":\">\",\"value\":35.0}]";
        
        Alert newAlert = new Alert();
        newAlert.setId(UUID.randomUUID());
        newAlert.setName("New Alert");
        newAlert.setHive(testHive);
        newAlert.setTriggerConditions(request.triggerConditions);
        newAlert.setIsTriggered(false);
        newAlert.setCreatedAt(LocalDateTime.now());
        
        when(alertService.createAlert(any(Alert.class), anyString())).thenReturn(newAlert);

        // Act & Assert
        mockMvc.perform(post("/api/alerts")
                .header("Authorization", validToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("New Alert"));
    }

    @Test
    void testUpdateAlert_Success() throws Exception {
        // Arrange
        AlertController.AlertRequest request = new AlertController.AlertRequest();
        request.name = "Updated Alert";
        request.hiveId = testHive.getId();
        request.triggerConditions = "[{\"parameter\":\"temperature\",\"operator\":\">\",\"value\":40.0}]";
        
        Alert updatedAlert = new Alert();
        updatedAlert.setId(testAlert.getId());
        updatedAlert.setName("Updated Alert");
        updatedAlert.setHive(testHive);
        updatedAlert.setTriggerConditions(request.triggerConditions);
        updatedAlert.setIsTriggered(false);
        updatedAlert.setCreatedAt(testAlert.getCreatedAt());
        
        when(alertService.updateAlert(any(UUID.class), any(Alert.class), anyString()))
                .thenReturn(updatedAlert);

        // Act & Assert
        mockMvc.perform(put("/api/alerts/{id}", testAlert.getId())
                .header("Authorization", validToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Updated Alert"));
    }

    @Test
    void testDeleteAlert_Success() throws Exception {
        // Arrange - deleteAlert returns void

        // Act & Assert
        mockMvc.perform(delete("/api/alerts/{id}", testAlert.getId())
                .header("Authorization", validToken))
                .andExpect(status().isNoContent());
    }

    @Test
    void testResetAlert_Success() throws Exception {
        // Arrange
        Alert resetAlert = new Alert();
        resetAlert.setId(testAlert.getId());
        resetAlert.setName(testAlert.getName());
        resetAlert.setHive(testHive);
        resetAlert.setTriggerConditions(testAlert.getTriggerConditions());
        resetAlert.setIsTriggered(false); // Reset to false
        resetAlert.setCreatedAt(testAlert.getCreatedAt());
        
        when(alertService.resetAlert(any(UUID.class), anyString())).thenReturn(resetAlert);

        // Act & Assert
        mockMvc.perform(post("/api/alerts/{id}/reset", testAlert.getId())
                .header("Authorization", validToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Test Alert"));
    }

    @Test
    void testGetAlertById_NotFound_ReturnsError() throws Exception {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();
        when(alertService.getAlertById(nonExistentId, testEmail))
                .thenThrow(new RuntimeException("Alert not found"));

        // Act & Assert
        mockMvc.perform(get("/api/alerts/{id}", nonExistentId)
                .header("Authorization", validToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Alert not found"))
                .andExpect(jsonPath("$.message").value("Alert not found"));
    }
}

