package com.beehivemonitor.controller;

import com.beehivemonitor.entity.Hive;
import com.beehivemonitor.entity.HiveSensorData;
import com.beehivemonitor.entity.User;
import com.beehivemonitor.repository.UserRepository;
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

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * API Test for GraphicsController
 * Tests REST API endpoints using MockMvc
 */
@WebMvcTest(controllers = GraphicsController.class, excludeAutoConfiguration = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
        org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration.class
})
@AutoConfigureMockMvc(addFilters = false)
class GraphicsControllerApiTest {

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

    @MockBean
    private UserRepository userRepository;

    private String validToken;
    private String testEmail;
    private UUID testHiveId;
    private HiveSensorData testHiveSensorData;
    private Hive testHive;

    @BeforeEach
    void setUp() {
        validToken = "Bearer valid-jwt-token";
        testEmail = "test@example.com";
        testHiveId = UUID.randomUUID();

        User testUser = new User();
        testUser.setId(UUID.randomUUID());
        testUser.setEmail(testEmail);
        testUser.setRole(User.Role.USER);

        testHive = new Hive();
        testHive.setId(testHiveId);
        testHive.setName("Test Hive");
        testHive.setLocation("Test Location");
        testHive.setUser(testUser);

        testHiveSensorData = new HiveSensorData();
        testHiveSensorData.setId(UUID.randomUUID());
        testHiveSensorData.setHive(testHive);
        testHiveSensorData.setTemperature(35.5);
        testHiveSensorData.setExternalTemperature(25.0);
        testHiveSensorData.setHumidity(60.0);
        testHiveSensorData.setCo2(500.0);
        testHiveSensorData.setSoundLevel(70.0);
        testHiveSensorData.setWeight(20.5);
        testHiveSensorData.setTimestamp(LocalDateTime.now());

        // Mock JWT token provider
        when(tokenProvider.getEmailFromToken(anyString())).thenReturn(testEmail);
        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(testUser));
    }

    @Test
    void testGetHistoricalData_Success() throws Exception {
        // Arrange
        List<HiveSensorData> historicalData = Arrays.asList(testHiveSensorData);
        when(sensorService.getHistoricalData(any(UUID.class), any(LocalDateTime.class), any(LocalDateTime.class), anyString()))
                .thenReturn(historicalData);

        // Act & Assert
        mockMvc.perform(get("/api/graphics/historical-data")
                .param("hiveId", testHiveId.toString())
                .param("days", "7")
                .header("Authorization", validToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].temperature").value(35.5))
                .andExpect(jsonPath("$[0].humidity").value(60.0))
                .andExpect(jsonPath("$[0].co2").value(500.0))
                .andExpect(jsonPath("$[0].hiveId").value(testHiveId.toString()));
    }

    @Test
    void testGetHistoricalData_MultipleRecords_Success() throws Exception {
        // Arrange
        HiveSensorData data1 = new HiveSensorData();
        data1.setId(UUID.randomUUID());
        data1.setHive(testHive);
        data1.setTemperature(35.0);
        data1.setExternalTemperature(25.0);
        data1.setHumidity(60.0);
        data1.setCo2(500.0);
        data1.setSoundLevel(70.0);
        data1.setWeight(20.0);
        data1.setTimestamp(LocalDateTime.now().minusHours(1));

        HiveSensorData data2 = new HiveSensorData();
        data2.setId(UUID.randomUUID());
        data2.setHive(testHive);
        data2.setTemperature(36.0);
        data2.setExternalTemperature(26.0);
        data2.setHumidity(65.0);
        data2.setCo2(550.0);
        data2.setSoundLevel(75.0);
        data2.setWeight(21.0);
        data2.setTimestamp(LocalDateTime.now());

        List<HiveSensorData> historicalData = Arrays.asList(data1, data2);
        when(sensorService.getHistoricalData(any(UUID.class), any(LocalDateTime.class), any(LocalDateTime.class), anyString()))
                .thenReturn(historicalData);

        // Act & Assert
        mockMvc.perform(get("/api/graphics/historical-data")
                .param("hiveId", testHiveId.toString())
                .param("days", "1")
                .header("Authorization", validToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].temperature").value(35.0))
                .andExpect(jsonPath("$[1].temperature").value(36.0));
    }

    @Test
    void testGetHistoricalData_EmptyResult_Success() throws Exception {
        // Arrange
        List<HiveSensorData> emptyData = Arrays.asList();
        when(sensorService.getHistoricalData(any(UUID.class), any(LocalDateTime.class), any(LocalDateTime.class), anyString()))
                .thenReturn(emptyData);

        // Act & Assert
        mockMvc.perform(get("/api/graphics/historical-data")
                .param("hiveId", testHiveId.toString())
                .param("days", "7")
                .header("Authorization", validToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void testGetHistoricalData_MissingHiveId_ReturnsBadRequest() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/graphics/historical-data")
                .param("days", "7")
                .header("Authorization", validToken))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetHistoricalData_MissingDays_ReturnsBadRequest() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/graphics/historical-data")
                .param("hiveId", testHiveId.toString())
                .header("Authorization", validToken))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetHistoricalData_InvalidDays_ReturnsBadRequest() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/graphics/historical-data")
                .param("hiveId", testHiveId.toString())
                .param("days", "invalid")
                .header("Authorization", validToken))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetHistoricalData_MissingToken_ReturnsBadRequest() throws Exception {
        // Act & Assert
        // Spring returns 400 Bad Request when required header is missing
        mockMvc.perform(get("/api/graphics/historical-data")
                .param("hiveId", testHiveId.toString())
                .param("days", "7"))
                .andExpect(status().isBadRequest());
    }
}

