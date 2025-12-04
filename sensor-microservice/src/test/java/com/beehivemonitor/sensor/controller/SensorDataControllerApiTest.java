package com.beehivemonitor.sensor.controller;

import com.beehivemonitor.sensor.dto.HiveSensorDataDTO;
import com.beehivemonitor.sensor.dto.RealtimeDataRequest;
import com.beehivemonitor.sensor.dto.RealtimeDataResponse;
import com.beehivemonitor.sensor.service.SensorDataService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * API Test for SensorDataController
 * Tests REST API endpoints using MockMvc
 */
@WebMvcTest(controllers = SensorDataController.class)
class SensorDataControllerApiTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SensorDataService sensorDataService;

    @Autowired
    private ObjectMapper objectMapper;

    private HiveSensorDataDTO mockSensorData;

    @BeforeEach
    void setUp() {
        mockSensorData = new HiveSensorDataDTO(25.5, 24.0, 55.0, 850.0, 65.0, 8.5);
    }

    @Test
    void testGetSensorDataForHive_Success() throws Exception {
        // Arrange
        Long hiveId = 1L;
        when(sensorDataService.generateSensorData()).thenReturn(mockSensorData);

        // Act & Assert
        mockMvc.perform(get("/api/sensor-data/hive/{hiveId}", hiveId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.temperature").value(25.5))
                .andExpect(jsonPath("$.externalTemperature").value(24.0))
                .andExpect(jsonPath("$.humidity").value(55.0))
                .andExpect(jsonPath("$.co2").value(850.0))
                .andExpect(jsonPath("$.soundLevel").value(65.0))
                .andExpect(jsonPath("$.weight").value(8.5));
    }

    @Test
    void testGetSensorDataForHive_DifferentHiveId_StillWorks() throws Exception {
        // Arrange
        Long hiveId = 999L;
        when(sensorDataService.generateSensorData()).thenReturn(mockSensorData);

        // Act & Assert
        mockMvc.perform(get("/api/sensor-data/hive/{hiveId}", hiveId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.temperature").exists());
    }

    @Test
    void testPostRealtimeData_SingleHive_Success() throws Exception {
        // Arrange
        RealtimeDataRequest request = new RealtimeDataRequest();
        request.setHiveIds(Collections.singletonList(1L));

        Map<Long, HiveSensorDataDTO> sensorDataMap = new HashMap<>();
        sensorDataMap.put(1L, mockSensorData);

        when(sensorDataService.generateSensorDataForHives(anyList())).thenReturn(sensorDataMap);

        // Act & Assert
        mockMvc.perform(post("/api/sensor-data/realtime")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.sensorData").exists())
                .andExpect(jsonPath("$.sensorData.1").exists())
                .andExpect(jsonPath("$.sensorData.1.temperature").value(25.5));
    }

    @Test
    void testPostRealtimeData_MultipleHives_Success() throws Exception {
        // Arrange
        RealtimeDataRequest request = new RealtimeDataRequest();
        request.setHiveIds(Arrays.asList(1L, 2L, 3L));

        HiveSensorDataDTO data2 = new HiveSensorDataDTO(26.0, 25.0, 56.0, 900.0, 70.0, 9.0);
        HiveSensorDataDTO data3 = new HiveSensorDataDTO(27.0, 26.0, 57.0, 950.0, 75.0, 9.5);

        Map<Long, HiveSensorDataDTO> sensorDataMap = new HashMap<>();
        sensorDataMap.put(1L, mockSensorData);
        sensorDataMap.put(2L, data2);
        sensorDataMap.put(3L, data3);

        when(sensorDataService.generateSensorDataForHives(anyList())).thenReturn(sensorDataMap);

        // Act & Assert
        mockMvc.perform(post("/api/sensor-data/realtime")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.sensorData").exists())
                .andExpect(jsonPath("$.sensorData.1").exists())
                .andExpect(jsonPath("$.sensorData.2").exists())
                .andExpect(jsonPath("$.sensorData.3").exists())
                .andExpect(jsonPath("$.sensorData.1.temperature").value(25.5))
                .andExpect(jsonPath("$.sensorData.2.temperature").value(26.0))
                .andExpect(jsonPath("$.sensorData.3.temperature").value(27.0));
    }

    @Test
    void testPostRealtimeData_EmptyHiveList_ReturnsEmptyMap() throws Exception {
        // Arrange
        RealtimeDataRequest request = new RealtimeDataRequest();
        request.setHiveIds(Collections.emptyList());

        Map<Long, HiveSensorDataDTO> emptyMap = new HashMap<>();
        when(sensorDataService.generateSensorDataForHives(anyList())).thenReturn(emptyMap);

        // Act & Assert
        mockMvc.perform(post("/api/sensor-data/realtime")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.sensorData").exists())
                .andExpect(jsonPath("$.sensorData").isEmpty());
    }

    @Test
    void testGetRealtimeData_SingleHive_Success() throws Exception {
        // Arrange
        List<Long> hiveIds = Collections.singletonList(1L);

        Map<Long, HiveSensorDataDTO> sensorDataMap = new HashMap<>();
        sensorDataMap.put(1L, mockSensorData);

        when(sensorDataService.generateSensorDataForHives(anyList())).thenReturn(sensorDataMap);

        // Act & Assert
        mockMvc.perform(get("/api/sensor-data/realtime")
                .param("hiveIds", "1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.sensorData").exists())
                .andExpect(jsonPath("$.sensorData.1").exists())
                .andExpect(jsonPath("$.sensorData.1.temperature").value(25.5));
    }

    @Test
    void testGetRealtimeData_MultipleHives_Success() throws Exception {
        // Arrange
        List<Long> hiveIds = Arrays.asList(1L, 2L);

        HiveSensorDataDTO data2 = new HiveSensorDataDTO(26.0, 25.0, 56.0, 900.0, 70.0, 9.0);

        Map<Long, HiveSensorDataDTO> sensorDataMap = new HashMap<>();
        sensorDataMap.put(1L, mockSensorData);
        sensorDataMap.put(2L, data2);

        when(sensorDataService.generateSensorDataForHives(anyList())).thenReturn(sensorDataMap);

        // Act & Assert
        mockMvc.perform(get("/api/sensor-data/realtime")
                .param("hiveIds", "1", "2"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.sensorData").exists())
                .andExpect(jsonPath("$.sensorData.1").exists())
                .andExpect(jsonPath("$.sensorData.2").exists());
    }

    @Test
    void testGetRealtimeData_MissingParameter_ReturnsBadRequest() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/sensor-data/realtime"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testPostRealtimeData_InvalidJson_ReturnsBadRequest() throws Exception {
        // Arrange
        String invalidJson = "{ invalid json }";

        // Act & Assert
        mockMvc.perform(post("/api/sensor-data/realtime")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetSensorDataForHive_NullHiveId_ReturnsNotFound() throws Exception {
        // Act & Assert - Path variable will be null/empty string
        mockMvc.perform(get("/api/sensor-data/hive/"))
                .andExpect(status().isNotFound());
    }
}

