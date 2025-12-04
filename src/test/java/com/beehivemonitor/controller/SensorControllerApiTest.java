package com.beehivemonitor.controller;

import com.beehivemonitor.controller.SensorController.HiveSensorData;
import com.beehivemonitor.dto.SensorReadingDTO;
import com.beehivemonitor.entity.User;
import com.beehivemonitor.security.CustomUserDetailsService;
import com.beehivemonitor.security.JwtAuthenticationFilter;
import com.beehivemonitor.security.JwtTokenProvider;
import com.beehivemonitor.service.SensorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * API Test for SensorController
 * Tests REST API endpoints using MockMvc
 */
@WebMvcTest(controllers = SensorController.class, excludeAutoConfiguration = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
        org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration.class
})
@AutoConfigureMockMvc(addFilters = false)
class SensorControllerApiTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SensorService sensorService;

    @MockBean
    private JwtTokenProvider tokenProvider;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private String validToken;
    private String testEmail;
    private UUID testHiveId;
    private HiveSensorData testSensorData;

    @BeforeEach
    void setUp() {
        validToken = "Bearer valid-jwt-token";
        testEmail = "test@example.com";
        testHiveId = UUID.randomUUID();
        
        testSensorData = new HiveSensorData(
            35.5,  // temperature
            25.0,  // externalTemperature
            60.0,  // humidity
            500.0, // co2
            70.0,  // soundLevel
            20.5   // weight
        );

        // Mock JWT token provider
        when(tokenProvider.getEmailFromToken(anyString())).thenReturn(testEmail);
    }

    @Test
    void testGetLastReadings_Success() throws Exception {
        // Arrange
        List<SensorReadingDTO> readings = Arrays.asList(
            new SensorReadingDTO("TEMPERATURE", 35.5, "°C"),
            new SensorReadingDTO("HUMIDITY", 60.0, "%")
        );
        when(sensorService.getLatestReadingsByHiveId(testHiveId, testEmail)).thenReturn(readings);

        // Act & Assert
        mockMvc.perform(get("/api/sensors/last-readings")
                .param("hiveId", testHiveId.toString())
                .header("Authorization", validToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].type").value("TEMPERATURE"))
                .andExpect(jsonPath("$[0].value").value(35.5));
    }

    @Test
    void testGetRealtimeDataForAllHives_Success() throws Exception {
        // Arrange
        Map<UUID, HiveSensorData> realtimeData = new HashMap<>();
        realtimeData.put(testHiveId, testSensorData);
        when(sensorService.getRealtimeDataForAllHives(testEmail)).thenReturn(realtimeData);

        // Act & Assert
        mockMvc.perform(get("/api/sensors/realtime-data")
                .header("Authorization", validToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$." + testHiveId + ".temperature").value(35.5));
    }

    @Test
    void testGetRealtimeDataForHive_Success() throws Exception {
        // Arrange
        when(sensorService.getRealtimeSensorDataForHive(testHiveId, testEmail))
                .thenReturn(testSensorData);

        // Act & Assert
        mockMvc.perform(get("/api/sensors/realtime-data/hive/{hiveId}", testHiveId)
                .header("Authorization", validToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.temperature").value(35.5))
                .andExpect(jsonPath("$.humidity").value(60.0))
                .andExpect(jsonPath("$.co2").value(500.0));
    }

    @Test
    void testUpdateAllSensors_Admin_Success() throws Exception {
        // Arrange
        Map<UUID, HiveSensorData> updatedData = new HashMap<>();
        updatedData.put(testHiveId, testSensorData);
        
        when(sensorService.updateAllSensorData(anyString())).thenReturn(updatedData);

        // Act & Assert
        mockMvc.perform(post("/api/sensors/update")
                .header("Authorization", validToken))
                .andExpect(status().isOk()) // Admin role check handled by security
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void testUpdateAllSensors_EmptyData_ReturnsMessage() throws Exception {
        // Arrange
        Map<UUID, HiveSensorData> emptyData = new HashMap<>();
        when(sensorService.updateAllSensorData(anyString())).thenReturn(emptyData);

        // Act & Assert
        mockMvc.perform(post("/api/sensors/update")
                .header("Authorization", validToken))
                .andExpect(status().isOk()) // Admin role check handled by security
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void testGetLastReadings_MissingHiveId_ReturnsBadRequest() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/sensors/last-readings")
                .header("Authorization", validToken))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetLastReadings_MissingToken_ReturnsBadRequest() throws Exception {
        // Act & Assert
        // Spring returns 400 Bad Request when required header is missing
        mockMvc.perform(get("/api/sensors/last-readings")
                .param("hiveId", testHiveId.toString()))
                .andExpect(status().isBadRequest());
    }
}

