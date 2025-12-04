package com.beehivemonitor.repository;

import com.beehivemonitor.entity.Alert;
import com.beehivemonitor.entity.Hive;
import com.beehivemonitor.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration Test for AlertRepository
 * Tests repository methods with actual database operations using H2 in-memory database
 */
@DataJpaTest
@ActiveProfiles("test")
class AlertRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private AlertRepository alertRepository;

    @Autowired
    private HiveRepository hiveRepository;

    @Autowired
    private UserRepository userRepository;

    private User testUser;
    private Hive testHive;

    @BeforeEach
    void setUp() {
        // Create and persist a test user
        testUser = new User();
        testUser.setName("Test User");
        testUser.setEmail("test@example.com");
        testUser.setPassword("encodedPassword");
        testUser.setRole(User.Role.USER);
        testUser.setEmailNotificationEnabled(false);
        testUser = entityManager.persistAndFlush(testUser);

        // Create and persist a test hive
        testHive = new Hive();
        testHive.setName("Test Hive");
        testHive.setLocation("Test Location");
        testHive.setUser(testUser);
        testHive = entityManager.persistAndFlush(testHive);
    }

    @Test
    void testFindByUserId_ReturnsUserAlerts() {
        // Arrange - Create multiple alerts
        Alert alert1 = new Alert();
        alert1.setName("Alert 1");
        alert1.setHive(testHive);
        alert1.setTriggerConditions("[{\"parameter\":\"temperature\",\"operator\":\">\",\"value\":30}]");
        alert1.setIsTriggered(false);
        entityManager.persistAndFlush(alert1);

        Alert alert2 = new Alert();
        alert2.setName("Alert 2");
        alert2.setHive(testHive);
        alert2.setTriggerConditions("[{\"parameter\":\"humidity\",\"operator\":\">\",\"value\":80}]");
        alert2.setIsTriggered(true);
        entityManager.persistAndFlush(alert2);

        // Act
        List<Alert> alerts = alertRepository.findByUserId(testUser.getId());

        // Assert
        assertNotNull(alerts);
        assertTrue(alerts.size() >= 2);
        assertTrue(alerts.stream().anyMatch(a -> a.getName().equals("Alert 1")));
        assertTrue(alerts.stream().anyMatch(a -> a.getName().equals("Alert 2")));
    }

    @Test
    void testSaveAlert_PersistsSuccessfully() {
        // Arrange
        Alert alert = new Alert();
        alert.setName("Test Alert");
        alert.setHive(testHive);
        alert.setTriggerConditions("[{\"parameter\":\"temperature\",\"operator\":\">\",\"value\":30}]");
        alert.setIsTriggered(false);

        // Act
        Alert savedAlert = alertRepository.save(alert);
        entityManager.flush();
        entityManager.clear();

        // Assert
        assertNotNull(savedAlert.getId());
        Alert foundAlert = entityManager.find(Alert.class, savedAlert.getId());
        assertNotNull(foundAlert);
        assertEquals("Test Alert", foundAlert.getName());
        assertFalse(foundAlert.getIsTriggered());
    }

    @Test
    void testFindById_ExistingAlert_ReturnsAlert() {
        // Arrange
        Alert alert = new Alert();
        alert.setName("Test Alert");
        alert.setHive(testHive);
        alert.setTriggerConditions("[{\"parameter\":\"temperature\",\"operator\":\">\",\"value\":30}]");
        Alert savedAlert = entityManager.persistAndFlush(alert);
        UUID alertId = savedAlert.getId();

        // Act
        Alert foundAlert = alertRepository.findById(alertId)
                .orElse(null);

        // Assert
        assertNotNull(foundAlert);
        assertEquals(alertId, foundAlert.getId());
        assertEquals("Test Alert", foundAlert.getName());
    }

    @Test
    void testFindById_NonExistentAlert_ReturnsEmpty() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();

        // Act
        var result = alertRepository.findById(nonExistentId);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void testUpdateAlert_UpdatesFields() {
        // Arrange
        Alert alert = new Alert();
        alert.setName("Original Alert");
        alert.setHive(testHive);
        alert.setTriggerConditions("[{\"parameter\":\"temperature\",\"operator\":\">\",\"value\":30}]");
        alert.setIsTriggered(false);
        Alert savedAlert = entityManager.persistAndFlush(alert);

        // Act - Update the alert
        savedAlert.setName("Updated Alert");
        savedAlert.setIsTriggered(true);
        savedAlert.setTriggerConditions("[{\"parameter\":\"humidity\",\"operator\":\">\",\"value\":80}]");
        Alert updatedAlert = alertRepository.save(savedAlert);
        entityManager.flush();
        entityManager.clear();

        // Assert
        Alert foundAlert = entityManager.find(Alert.class, updatedAlert.getId());
        assertNotNull(foundAlert);
        assertEquals("Updated Alert", foundAlert.getName());
        assertTrue(foundAlert.getIsTriggered());
    }

    @Test
    void testDeleteAlert_RemovesFromDatabase() {
        // Arrange
        Alert alert = new Alert();
        alert.setName("To Be Deleted");
        alert.setHive(testHive);
        alert.setTriggerConditions("[{\"parameter\":\"temperature\",\"operator\":\">\",\"value\":30}]");
        Alert savedAlert = entityManager.persistAndFlush(alert);
        UUID alertId = savedAlert.getId();

        // Act
        alertRepository.delete(savedAlert);
        entityManager.flush();
        entityManager.clear();

        // Assert
        Alert foundAlert = entityManager.find(Alert.class, alertId);
        assertNull(foundAlert);
    }

    @Test
    void testAlertHiveRelationship_IsMaintained() {
        // Arrange
        Alert alert = new Alert();
        alert.setName("Relationship Test");
        alert.setHive(testHive);
        alert.setTriggerConditions("[{\"parameter\":\"temperature\",\"operator\":\">\",\"value\":30}]");
        Alert savedAlert = entityManager.persistAndFlush(alert);
        UUID alertId = savedAlert.getId();
        entityManager.clear();

        // Act
        Alert foundAlert = entityManager.find(Alert.class, alertId);

        // Assert
        assertNotNull(foundAlert);
        assertNotNull(foundAlert.getHive());
        assertEquals(testHive.getId(), foundAlert.getHive().getId());
    }
}

