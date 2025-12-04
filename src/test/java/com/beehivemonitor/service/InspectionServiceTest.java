package com.beehivemonitor.service;

import com.beehivemonitor.entity.Hive;
import com.beehivemonitor.entity.Inspection;
import com.beehivemonitor.entity.User;
import com.beehivemonitor.repository.HiveRepository;
import com.beehivemonitor.repository.InspectionRepository;
import com.beehivemonitor.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit Test for InspectionService
 * Tests business logic in isolation using Mockito
 */
@ExtendWith(MockitoExtension.class)
class InspectionServiceTest {

    @Mock
    private InspectionRepository inspectionRepository;

    @Mock
    private HiveRepository hiveRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private InspectionService inspectionService;

    private User testUser;
    private User otherUser;
    private Hive testHive;
    private Inspection testInspection;
    private UUID userId;
    private UUID hiveId;
    private UUID inspectionId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        hiveId = UUID.randomUUID();
        inspectionId = UUID.randomUUID();

        testUser = new User();
        testUser.setId(userId);
        testUser.setName("Test User");
        testUser.setEmail("test@example.com");

        otherUser = new User();
        otherUser.setId(UUID.randomUUID());
        otherUser.setName("Other User");
        otherUser.setEmail("other@example.com");

        testHive = new Hive();
        testHive.setId(hiveId);
        testHive.setName("Test Hive");
        testHive.setUser(testUser);

        testInspection = new Inspection();
        testInspection.setId(inspectionId);
        testInspection.setHive(testHive);
        testInspection.setInspector("Test Inspector");
        testInspection.setDate(LocalDate.of(2024, 1, 15));
        testInspection.setNotes("Test notes");
    }

    @Test
    void testGetAllInspectionsByUser_Success() {
        // Arrange
        Inspection inspection2 = new Inspection();
        inspection2.setId(UUID.randomUUID());
        inspection2.setHive(testHive);
        List<Inspection> inspections = Arrays.asList(testInspection, inspection2);
        
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(inspectionRepository.findByUserId(userId)).thenReturn(inspections);

        // Act
        List<Inspection> result = inspectionService.getAllInspectionsByUser("test@example.com");

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(userRepository, times(1)).findByEmail("test@example.com");
        verify(inspectionRepository, times(1)).findByUserId(userId);
    }

    @Test
    void testGetAllInspectionsByUser_UserNotFound_ThrowsException() {
        // Arrange
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            inspectionService.getAllInspectionsByUser("nonexistent@example.com");
        });

        assertEquals("User not found", exception.getMessage());
        verify(inspectionRepository, never()).findByUserId(any(UUID.class));
    }

    @Test
    void testGetInspectionById_Success() {
        // Arrange
        when(inspectionRepository.findById(inspectionId)).thenReturn(Optional.of(testInspection));

        // Act
        Inspection result = inspectionService.getInspectionById(inspectionId, "test@example.com");

        // Assert
        assertNotNull(result);
        assertEquals(testInspection.getId(), result.getId());
        verify(inspectionRepository, times(1)).findById(inspectionId);
    }

    @Test
    void testGetInspectionById_Unauthorized_ThrowsException() {
        // Arrange
        when(inspectionRepository.findById(inspectionId)).thenReturn(Optional.of(testInspection));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            inspectionService.getInspectionById(inspectionId, "other@example.com");
        });

        assertEquals("Unauthorized access to inspection", exception.getMessage());
    }

    @Test
    void testGetInspectionById_InspectionNotFound_ThrowsException() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();
        when(inspectionRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            inspectionService.getInspectionById(nonExistentId, "test@example.com");
        });

        assertEquals("Inspection not found", exception.getMessage());
    }

    @Test
    void testCreateInspection_Success() {
        // Arrange
        Inspection newInspection = new Inspection();
        newInspection.setInspector("New Inspector");
        newInspection.setDate(LocalDate.of(2024, 2, 1));
        newInspection.setNotes("New notes");
        newInspection.setHive(testHive);

        when(hiveRepository.findById(hiveId)).thenReturn(Optional.of(testHive));
        when(inspectionRepository.save(any(Inspection.class))).thenReturn(newInspection);

        // Act
        Inspection result = inspectionService.createInspection(newInspection, "test@example.com");

        // Assert
        assertNotNull(result);
        assertEquals(testHive, newInspection.getHive());
        verify(hiveRepository, times(1)).findById(hiveId);
        verify(inspectionRepository, times(1)).save(newInspection);
    }

    @Test
    void testCreateInspection_Unauthorized_ThrowsException() {
        // Arrange
        Inspection newInspection = new Inspection();
        newInspection.setHive(testHive);

        when(hiveRepository.findById(hiveId)).thenReturn(Optional.of(testHive));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            inspectionService.createInspection(newInspection, "other@example.com");
        });

        assertEquals("Unauthorized access to hive", exception.getMessage());
        verify(inspectionRepository, never()).save(any(Inspection.class));
    }

    @Test
    void testCreateInspection_HiveNotFound_ThrowsException() {
        // Arrange
        Inspection newInspection = new Inspection();
        newInspection.setHive(testHive);
        UUID nonExistentHiveId = UUID.randomUUID();
        newInspection.getHive().setId(nonExistentHiveId);

        when(hiveRepository.findById(nonExistentHiveId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            inspectionService.createInspection(newInspection, "test@example.com");
        });

        assertEquals("Hive not found", exception.getMessage());
        verify(inspectionRepository, never()).save(any(Inspection.class));
    }

    @Test
    void testUpdateInspection_Success() {
        // Arrange
        Inspection updatedInspection = new Inspection();
        updatedInspection.setInspector("Updated Inspector");
        updatedInspection.setDate(LocalDate.of(2024, 3, 1));
        updatedInspection.setNotes("Updated notes");

        when(inspectionRepository.findById(inspectionId)).thenReturn(Optional.of(testInspection));
        when(inspectionRepository.save(any(Inspection.class))).thenReturn(testInspection);

        // Act
        Inspection result = inspectionService.updateInspection(inspectionId, updatedInspection, "test@example.com");

        // Assert
        assertNotNull(result);
        assertEquals("Updated Inspector", testInspection.getInspector());
        assertEquals(LocalDate.of(2024, 3, 1), testInspection.getDate());
        assertEquals("Updated notes", testInspection.getNotes());
        verify(inspectionRepository, times(1)).save(testInspection);
    }

    @Test
    void testDeleteInspection_Success() {
        // Arrange
        when(inspectionRepository.findById(inspectionId)).thenReturn(Optional.of(testInspection));
        doNothing().when(inspectionRepository).delete(testInspection);

        // Act
        inspectionService.deleteInspection(inspectionId, "test@example.com");

        // Assert
        verify(inspectionRepository, times(1)).findById(inspectionId);
        verify(inspectionRepository, times(1)).delete(testInspection);
    }

    @Test
    void testDeleteInspection_Unauthorized_ThrowsException() {
        // Arrange
        when(inspectionRepository.findById(inspectionId)).thenReturn(Optional.of(testInspection));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            inspectionService.deleteInspection(inspectionId, "other@example.com");
        });

        assertEquals("Unauthorized access to inspection", exception.getMessage());
        verify(inspectionRepository, never()).delete(any(Inspection.class));
    }
}

