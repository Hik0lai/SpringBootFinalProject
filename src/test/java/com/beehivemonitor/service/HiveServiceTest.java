package com.beehivemonitor.service;

import com.beehivemonitor.entity.Hive;
import com.beehivemonitor.entity.User;
import com.beehivemonitor.repository.HiveRepository;
import com.beehivemonitor.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit Test for HiveService
 * Tests business logic in isolation using Mockito
 */
@ExtendWith(MockitoExtension.class)
class HiveServiceTest {

    @Mock
    private HiveRepository hiveRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private HiveService hiveService;

    private User testUser;
    private Hive testHive;
    private UUID hiveId;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        hiveId = UUID.randomUUID();

        testUser = new User();
        testUser.setId(userId);
        testUser.setName("Test User");
        testUser.setEmail("test@example.com");
        testUser.setRole(User.Role.ADMIN);

        testHive = new Hive();
        testHive.setId(hiveId);
        testHive.setName("Test Hive");
        testHive.setLocation("Test Location");
        testHive.setBirthDate("2024-01");
        testHive.setUser(testUser);
    }

    @Test
    void testGetAllHives_Success() {
        // Arrange
        Hive hive2 = new Hive();
        hive2.setId(UUID.randomUUID());
        hive2.setName("Hive 2");
        List<Hive> hives = Arrays.asList(testHive, hive2);
        when(hiveRepository.findAll()).thenReturn(hives);

        // Act
        List<Hive> result = hiveService.getAllHives();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(hiveRepository, times(1)).findAll();
    }

    @Test
    void testGetHiveById_Success() {
        // Arrange
        when(hiveRepository.findById(hiveId)).thenReturn(Optional.of(testHive));

        // Act
        Hive result = hiveService.getHiveById(hiveId);

        // Assert
        assertNotNull(result);
        assertEquals(testHive.getId(), result.getId());
        assertEquals(testHive.getName(), result.getName());
        verify(hiveRepository, times(1)).findById(hiveId);
    }

    @Test
    void testGetHiveById_NotFound_ThrowsException() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();
        when(hiveRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            hiveService.getHiveById(nonExistentId);
        });

        assertEquals("Hive not found", exception.getMessage());
        verify(hiveRepository, times(1)).findById(nonExistentId);
    }

    @Test
    void testCreateHive_Admin_Success() {
        // Arrange
        Hive newHive = new Hive();
        newHive.setName("New Hive");
        newHive.setLocation("New Location");
        when(userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(testUser));
        when(hiveRepository.save(any(Hive.class))).thenReturn(newHive);

        // Act
        Hive result = hiveService.createHive(newHive, "admin@example.com", User.Role.ADMIN);

        // Assert
        assertNotNull(result);
        assertEquals(testUser, newHive.getUser());
        verify(userRepository, times(1)).findByEmail("admin@example.com");
        verify(hiveRepository, times(1)).save(newHive);
    }

    @Test
    void testCreateHive_NonAdmin_ThrowsException() {
        // Arrange
        Hive newHive = new Hive();
        newHive.setName("New Hive");

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            hiveService.createHive(newHive, "user@example.com", User.Role.USER);
        });

        assertEquals("Only administrators can create hives", exception.getMessage());
        verify(userRepository, never()).findByEmail(anyString());
        verify(hiveRepository, never()).save(any(Hive.class));
    }

    @Test
    void testCreateHive_UserNotFound_ThrowsException() {
        // Arrange
        Hive newHive = new Hive();
        newHive.setName("New Hive");
        when(userRepository.findByEmail("admin@example.com")).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            hiveService.createHive(newHive, "admin@example.com", User.Role.ADMIN);
        });

        assertEquals("User not found", exception.getMessage());
        verify(hiveRepository, never()).save(any(Hive.class));
    }

    @Test
    void testUpdateHive_Admin_Success() {
        // Arrange
        Hive updatedHive = new Hive();
        updatedHive.setName("Updated Hive");
        updatedHive.setLocation("Updated Location");
        updatedHive.setBirthDate("2024-12");

        when(hiveRepository.findById(hiveId)).thenReturn(Optional.of(testHive));
        when(hiveRepository.save(any(Hive.class))).thenReturn(testHive);

        // Act
        Hive result = hiveService.updateHive(hiveId, updatedHive, User.Role.ADMIN);

        // Assert
        assertNotNull(result);
        assertEquals("Updated Hive", testHive.getName());
        assertEquals("Updated Location", testHive.getLocation());
        assertEquals("2024-12", testHive.getBirthDate());
        verify(hiveRepository, times(1)).findById(hiveId);
        verify(hiveRepository, times(1)).save(testHive);
    }

    @Test
    void testUpdateHive_NonAdmin_ThrowsException() {
        // Arrange
        Hive updatedHive = new Hive();
        updatedHive.setName("Updated Hive");

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            hiveService.updateHive(hiveId, updatedHive, User.Role.USER);
        });

        assertEquals("Only administrators can update hives", exception.getMessage());
        verify(hiveRepository, never()).save(any(Hive.class));
    }

    @Test
    void testDeleteHive_Admin_Success() {
        // Arrange
        when(hiveRepository.findById(hiveId)).thenReturn(Optional.of(testHive));
        doNothing().when(hiveRepository).delete(testHive);

        // Act
        hiveService.deleteHive(hiveId, User.Role.ADMIN);

        // Assert
        verify(hiveRepository, times(1)).findById(hiveId);
        verify(hiveRepository, times(1)).delete(testHive);
    }

    @Test
    void testDeleteHive_NonAdmin_ThrowsException() {
        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            hiveService.deleteHive(hiveId, User.Role.USER);
        });

        assertEquals("Only administrators can delete hives", exception.getMessage());
        verify(hiveRepository, never()).delete(any(Hive.class));
    }
}

