package com.beehivemonitor.controller;

import com.beehivemonitor.entity.Hive;
import com.beehivemonitor.entity.User;
import com.beehivemonitor.repository.UserRepository;
import com.beehivemonitor.security.CustomUserDetailsService;
import com.beehivemonitor.security.JwtAuthenticationFilter;
import com.beehivemonitor.security.JwtTokenProvider;
import com.beehivemonitor.service.HiveService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * API Test for HiveController
 * Tests REST API endpoints using MockMvc
 */
@WebMvcTest(controllers = HiveController.class, excludeAutoConfiguration = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
        org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration.class
})
@AutoConfigureMockMvc(addFilters = false)
class HiveControllerApiTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private HiveService hiveService;

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
    private Hive testHive;

    @BeforeEach
    void setUp() {
        validToken = "Bearer valid-jwt-token";
        testEmail = "test@example.com";
        
        testUser = new User();
        testUser.setId(UUID.randomUUID());
        testUser.setEmail(testEmail);
        testUser.setRole(User.Role.ADMIN);
        
        testHive = new Hive();
        testHive.setId(UUID.randomUUID());
        testHive.setName("Test Hive");
        testHive.setLocation("Test Location");
        testHive.setBirthDate("2024-01");
        testHive.setUser(testUser);

        // Mock JWT token provider
        when(tokenProvider.getEmailFromToken(anyString())).thenReturn(testEmail);
        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(testUser));
    }

    @Test
    void testGetAllHives_Success() throws Exception {
        // Arrange
        List<Hive> hives = Arrays.asList(testHive);
        when(hiveService.getAllHives()).thenReturn(hives);

        // Act & Assert
        mockMvc.perform(get("/api/hives")
                .header("Authorization", validToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].name").value("Test Hive"))
                .andExpect(jsonPath("$[0].location").value("Test Location"));
    }

    @Test
    void testGetHiveById_Success() throws Exception {
        // Arrange
        when(hiveService.getHiveById(testHive.getId())).thenReturn(testHive);

        // Act & Assert
        mockMvc.perform(get("/api/hives/{id}", testHive.getId())
                .header("Authorization", validToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(testHive.getId().toString()))
                .andExpect(jsonPath("$.name").value("Test Hive"))
                .andExpect(jsonPath("$.location").value("Test Location"));
    }

    @Test
    void testGetHiveById_NotFound_ReturnsNotFound() throws Exception {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();
        when(hiveService.getHiveById(nonExistentId))
                .thenThrow(new RuntimeException("Hive not found"));

        // Act & Assert
        mockMvc.perform(get("/api/hives/{id}", nonExistentId)
                .header("Authorization", validToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Hive not found"))
                .andExpect(jsonPath("$.message").value("Hive not found"));
    }

    @Test
    void testCreateHive_Admin_Success() throws Exception {
        // Arrange
        Hive newHive = new Hive();
        newHive.setName("New Hive");
        newHive.setLocation("New Location");
        newHive.setBirthDate("2024-02");
        
        Hive savedHive = new Hive();
        savedHive.setId(UUID.randomUUID());
        savedHive.setName("New Hive");
        savedHive.setLocation("New Location");
        savedHive.setBirthDate("2024-02");
        savedHive.setUser(testUser);
        
        when(hiveService.createHive(any(Hive.class), anyString(), any(User.Role.class)))
                .thenReturn(savedHive);

        // Act & Assert
        mockMvc.perform(post("/api/hives")
                .header("Authorization", validToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newHive)))
                .andExpect(status().isOk()) // Admin role check handled by security
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void testCreateHive_InvalidData_ReturnsBadRequest() throws Exception {
        // Arrange
        Hive invalidHive = new Hive();
        invalidHive.setName(""); // Invalid: empty name

        // Act & Assert
        mockMvc.perform(post("/api/hives")
                .header("Authorization", validToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidHive)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateHive_Admin_Success() throws Exception {
        // Arrange
        Hive updatedHive = new Hive();
        updatedHive.setName("Updated Hive");
        updatedHive.setLocation("Updated Location");
        updatedHive.setBirthDate("2024-03");
        
        Hive savedHive = new Hive();
        savedHive.setId(testHive.getId());
        savedHive.setName("Updated Hive");
        savedHive.setLocation("Updated Location");
        savedHive.setBirthDate("2024-03");
        savedHive.setUser(testUser);
        
        when(hiveService.updateHive(any(UUID.class), any(Hive.class), any(User.Role.class)))
                .thenReturn(savedHive);

        // Act & Assert
        mockMvc.perform(put("/api/hives/{id}", testHive.getId())
                .header("Authorization", validToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedHive)))
                .andExpect(status().isOk()); // Admin role check handled by security
    }

    @Test
    void testDeleteHive_Admin_Success() throws Exception {
        // Arrange - deleteHive returns void, so we don't need to mock return value

        // Act & Assert
        mockMvc.perform(delete("/api/hives/{id}", testHive.getId())
                .header("Authorization", validToken))
                .andExpect(status().isNoContent()); // Admin role check handled by security
    }

    @Test
    void testGetAllHives_MissingToken_ReturnsBadRequest() throws Exception {
        // Act & Assert
        // Spring returns 400 Bad Request when required header is missing
        mockMvc.perform(get("/api/hives"))
                .andExpect(status().isBadRequest());
    }
}

