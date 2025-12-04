package com.beehivemonitor.service;

import com.beehivemonitor.dto.AuthResponse;
import com.beehivemonitor.entity.User;
import com.beehivemonitor.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit Test for UserService
 * Tests business logic in isolation using Mockito
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private User adminUser;
    private UUID testUserId;
    private UUID adminUserId;

    @BeforeEach
    void setUp() {
        testUserId = UUID.randomUUID();
        adminUserId = UUID.randomUUID();

        testUser = new User();
        testUser.setId(testUserId);
        testUser.setName("Test User");
        testUser.setEmail("test@example.com");
        testUser.setPassword("encodedPassword");
        testUser.setRole(User.Role.USER);
        testUser.setEmailNotificationEnabled(false);

        adminUser = new User();
        adminUser.setId(adminUserId);
        adminUser.setName("Admin User");
        adminUser.setEmail("admin@example.com");
        adminUser.setPassword("encodedPassword");
        adminUser.setRole(User.Role.ADMIN);
        adminUser.setEmailNotificationEnabled(true);
    }

    @Test
    void testGetCurrentUser_Success() {
        // Arrange
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        // Act
        AuthResponse.UserResponse response = userService.getCurrentUser("test@example.com");

        // Assert
        assertNotNull(response);
        assertEquals(testUser.getEmail(), response.getEmail());
        assertEquals(testUser.getName(), response.getName());
        assertEquals(testUser.getRole(), response.getRole());
        verify(userRepository, times(1)).findByEmail("test@example.com");
    }

    @Test
    void testGetCurrentUser_UserNotFound_ThrowsException() {
        // Arrange
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.getCurrentUser("nonexistent@example.com");
        });

        assertEquals("User not found", exception.getMessage());
        verify(userRepository, times(1)).findByEmail("nonexistent@example.com");
    }

    @Test
    void testGetAllUsers_Success() {
        // Arrange
        List<User> users = Arrays.asList(testUser, adminUser);
        when(userRepository.findAll()).thenReturn(users);

        // Act
        List<AuthResponse.UserResponse> response = userService.getAllUsers();

        // Assert
        assertNotNull(response);
        assertEquals(2, response.size());
        assertEquals(testUser.getEmail(), response.get(0).getEmail());
        assertEquals(adminUser.getEmail(), response.get(1).getEmail());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void testGetAllUserNames_Success() {
        // Arrange
        List<User> users = Arrays.asList(testUser, adminUser);
        when(userRepository.findAll()).thenReturn(users);

        // Act
        List<com.beehivemonitor.controller.UserController.UserNameResponse> response = userService.getAllUserNames();

        // Assert
        assertNotNull(response);
        assertEquals(2, response.size());
        assertEquals(testUser.getId(), response.get(0).id);
        assertEquals(testUser.getName(), response.get(0).name);
        assertEquals(testUser.getEmail(), response.get(0).email);
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void testUpdateEmailNotificationPreference_Success() {
        // Arrange
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        userService.updateEmailNotificationPreference("test@example.com", true);

        // Assert
        assertTrue(testUser.getEmailNotificationEnabled());
        verify(userRepository, times(1)).findByEmail("test@example.com");
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void testUpdateEmailNotificationPreference_UserNotFound_ThrowsException() {
        // Arrange
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.updateEmailNotificationPreference("nonexistent@example.com", true);
        });

        assertEquals("User not found", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testUpdateUserRole_Success() {
        // Arrange
        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
        when(userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(adminUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        AuthResponse.UserResponse response = userService.updateUserRole(testUserId, User.Role.ADMIN, "admin@example.com");

        // Assert
        assertNotNull(response);
        assertEquals(User.Role.ADMIN, testUser.getRole());
        verify(userRepository, times(1)).findById(testUserId);
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void testUpdateUserRole_AdminCannotRemoveOwnRole_ThrowsException() {
        // Arrange
        when(userRepository.findById(adminUserId)).thenReturn(Optional.of(adminUser));
        when(userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(adminUser));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.updateUserRole(adminUserId, User.Role.USER, "admin@example.com");
        });

        assertEquals("You cannot remove admin role from yourself. Please ask another admin to do it.", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testUpdateUserRole_UserNotFound_ThrowsException() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();
        when(userRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.updateUserRole(nonExistentId, User.Role.ADMIN, "admin@example.com");
        });

        assertEquals("User not found", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testChangePassword_Success() {
        // Arrange
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("oldPassword", "encodedPassword")).thenReturn(true);
        when(passwordEncoder.matches("newPassword123", "encodedPassword")).thenReturn(false);
        when(passwordEncoder.encode("newPassword123")).thenReturn("newEncodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        userService.changePassword("test@example.com", "oldPassword", "newPassword123");

        // Assert
        verify(passwordEncoder, times(1)).matches("oldPassword", "encodedPassword");
        verify(passwordEncoder, times(1)).encode("newPassword123");
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void testChangePassword_CurrentPasswordIncorrect_ThrowsException() {
        // Arrange
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrongPassword", "encodedPassword")).thenReturn(false);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.changePassword("test@example.com", "wrongPassword", "newPassword123");
        });

        assertEquals("Current password is incorrect. Please verify your current password and try again.", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testChangePassword_NewPasswordTooShort_ThrowsException() {
        // Arrange
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("oldPassword", "encodedPassword")).thenReturn(true);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.changePassword("test@example.com", "oldPassword", "short");
        });

        assertEquals("New password must be at least 6 characters long", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testChangePassword_NewPasswordSameAsCurrent_ThrowsException() {
        // Arrange
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("oldPassword", "encodedPassword")).thenReturn(true);
        when(passwordEncoder.matches("oldPassword", "encodedPassword")).thenReturn(true);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.changePassword("test@example.com", "oldPassword", "oldPassword");
        });

        assertEquals("New password must be different from your current password", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testChangePassword_CurrentPasswordNull_ThrowsException() {
        // Arrange
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.changePassword("test@example.com", null, "newPassword123");
        });

        assertEquals("Current password is required", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testChangePassword_NewPasswordNull_ThrowsException() {
        // Arrange
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.changePassword("test@example.com", "oldPassword", null);
        });

        assertEquals("New password cannot be empty", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testChangePassword_UserNotFound_ThrowsException() {
        // Arrange
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.changePassword("nonexistent@example.com", "oldPassword", "newPassword123");
        });

        assertEquals("User not found", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }
}

