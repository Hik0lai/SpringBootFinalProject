package com.beehivemonitor.controller;

import com.beehivemonitor.entity.Hive;
import com.beehivemonitor.entity.Inspection;
import com.beehivemonitor.entity.User;
import com.beehivemonitor.repository.HiveRepository;
import com.beehivemonitor.security.CustomUserDetailsService;
import com.beehivemonitor.security.JwtAuthenticationFilter;
import com.beehivemonitor.security.JwtTokenProvider;
import com.beehivemonitor.service.InspectionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
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
 * API Test for InspectionController
 * Tests REST API endpoints using MockMvc
 */
@WebMvcTest(controllers = InspectionController.class, excludeAutoConfiguration = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
        org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration.class
})
@AutoConfigureMockMvc(addFilters = false)
class InspectionControllerApiTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InspectionService inspectionService;

    @MockBean
    private JwtTokenProvider tokenProvider;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private HiveRepository hiveRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private String validToken;
    private String testEmail;
    private User testUser;
    private Hive testHive;
    private Inspection testInspection;

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
        
        testInspection = new Inspection();
        testInspection.setId(UUID.randomUUID());
        testInspection.setHive(testHive);
        testInspection.setInspector("Test Inspector");
        testInspection.setDate(LocalDate.now());
        testInspection.setNotes("Test Notes");

        // Mock JWT token provider
        when(tokenProvider.getEmailFromToken(anyString())).thenReturn(testEmail);
    }

    @Test
    void testGetAllInspections_Success() throws Exception {
        // Arrange
        List<Inspection> inspections = Arrays.asList(testInspection);
        when(inspectionService.getAllInspectionsByUser(testEmail)).thenReturn(inspections);

        // Act & Assert
        mockMvc.perform(get("/api/inspections")
                .header("Authorization", validToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].inspector").value("Test Inspector"))
                .andExpect(jsonPath("$[0].notes").value("Test Notes"));
    }

    @Test
    void testGetInspectionById_Success() throws Exception {
        // Arrange
        when(inspectionService.getInspectionById(testInspection.getId(), testEmail))
                .thenReturn(testInspection);

        // Act & Assert
        mockMvc.perform(get("/api/inspections/{id}", testInspection.getId())
                .header("Authorization", validToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(testInspection.getId().toString()))
                .andExpect(jsonPath("$.inspector").value("Test Inspector"));
    }

    @Test
    void testCreateInspection_Success() throws Exception {
        // Arrange
        InspectionController.InspectionRequest request = new InspectionController.InspectionRequest();
        request.setHiveId(testHive.getId());
        request.setInspector("New Inspector");
        request.setDate(LocalDate.now().toString());
        request.setNotes("New Notes");
        
        Inspection newInspection = new Inspection();
        newInspection.setId(UUID.randomUUID());
        newInspection.setHive(testHive);
        newInspection.setInspector("New Inspector");
        newInspection.setDate(LocalDate.now());
        newInspection.setNotes("New Notes");
        
        when(hiveRepository.findById(testHive.getId())).thenReturn(Optional.of(testHive));
        when(inspectionService.createInspection(any(Inspection.class), anyString()))
                .thenReturn(newInspection);

        // Act & Assert
        mockMvc.perform(post("/api/inspections")
                .header("Authorization", validToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.inspector").value("New Inspector"));
    }

    @Test
    void testUpdateInspection_Success() throws Exception {
        // Arrange
        InspectionController.InspectionRequest request = new InspectionController.InspectionRequest();
        request.setInspector("Updated Inspector");
        request.setDate(LocalDate.now().toString());
        request.setNotes("Updated Notes");
        
        Inspection updatedInspection = new Inspection();
        updatedInspection.setId(testInspection.getId());
        updatedInspection.setHive(testHive);
        updatedInspection.setInspector("Updated Inspector");
        updatedInspection.setDate(LocalDate.now());
        updatedInspection.setNotes("Updated Notes");
        
        when(inspectionService.updateInspection(any(UUID.class), any(Inspection.class), anyString()))
                .thenReturn(updatedInspection);

        // Act & Assert
        mockMvc.perform(put("/api/inspections/{id}", testInspection.getId())
                .header("Authorization", validToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.inspector").value("Updated Inspector"));
    }

    @Test
    void testDeleteInspection_Success() throws Exception {
        // Arrange - deleteInspection returns void

        // Act & Assert
        mockMvc.perform(delete("/api/inspections/{id}", testInspection.getId())
                .header("Authorization", validToken))
                .andExpect(status().isNoContent());
    }

    @Test
    void testGetInspectionById_NotFound_ReturnsError() throws Exception {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();
        when(inspectionService.getInspectionById(nonExistentId, testEmail))
                .thenThrow(new RuntimeException("Inspection not found"));

        // Act & Assert
        mockMvc.perform(get("/api/inspections/{id}", nonExistentId)
                .header("Authorization", validToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Inspection not found"))
                .andExpect(jsonPath("$.message").value("Inspection not found"));
    }

    @Test
    void testCreateInspection_InvalidRequest_ReturnsBadRequest() throws Exception {
        // Arrange
        InspectionController.InspectionRequest request = new InspectionController.InspectionRequest();
        // Missing required fields

        // Act & Assert
        mockMvc.perform(post("/api/inspections")
                .header("Authorization", validToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}

