package com.beehivemonitor.repository;

import com.beehivemonitor.entity.Hive;
import com.beehivemonitor.entity.Inspection;
import com.beehivemonitor.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration Test for InspectionRepository
 * Tests repository methods with actual database operations using H2 in-memory database
 */
@DataJpaTest
@ActiveProfiles("test")
class InspectionRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private InspectionRepository inspectionRepository;

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
    void testFindByUserId_ReturnsUserInspections() {
        // Arrange - Create multiple inspections
        Inspection inspection1 = new Inspection();
        inspection1.setHive(testHive);
        inspection1.setInspector("Inspector 1");
        inspection1.setDate(LocalDate.of(2024, 1, 15));
        inspection1.setNotes("Notes 1");
        entityManager.persistAndFlush(inspection1);

        Inspection inspection2 = new Inspection();
        inspection2.setHive(testHive);
        inspection2.setInspector("Inspector 2");
        inspection2.setDate(LocalDate.of(2024, 2, 20));
        inspection2.setNotes("Notes 2");
        entityManager.persistAndFlush(inspection2);

        // Act
        List<Inspection> inspections = inspectionRepository.findByUserId(testUser.getId());

        // Assert
        assertNotNull(inspections);
        assertTrue(inspections.size() >= 2);
        assertTrue(inspections.stream().anyMatch(i -> i.getInspector().equals("Inspector 1")));
        assertTrue(inspections.stream().anyMatch(i -> i.getInspector().equals("Inspector 2")));
    }

    @Test
    void testSaveInspection_PersistsSuccessfully() {
        // Arrange
        Inspection inspection = new Inspection();
        inspection.setHive(testHive);
        inspection.setInspector("Test Inspector");
        inspection.setDate(LocalDate.of(2024, 3, 10));
        inspection.setNotes("Test notes");

        // Act
        Inspection savedInspection = inspectionRepository.save(inspection);
        entityManager.flush();
        entityManager.clear();

        // Assert
        assertNotNull(savedInspection.getId());
        Inspection foundInspection = entityManager.find(Inspection.class, savedInspection.getId());
        assertNotNull(foundInspection);
        assertEquals("Test Inspector", foundInspection.getInspector());
        assertEquals(LocalDate.of(2024, 3, 10), foundInspection.getDate());
        assertEquals("Test notes", foundInspection.getNotes());
    }

    @Test
    void testFindById_ExistingInspection_ReturnsInspection() {
        // Arrange
        Inspection inspection = new Inspection();
        inspection.setHive(testHive);
        inspection.setInspector("Test Inspector");
        inspection.setDate(LocalDate.of(2024, 1, 1));
        Inspection savedInspection = entityManager.persistAndFlush(inspection);
        UUID inspectionId = savedInspection.getId();

        // Act
        Inspection foundInspection = inspectionRepository.findById(inspectionId)
                .orElse(null);

        // Assert
        assertNotNull(foundInspection);
        assertEquals(inspectionId, foundInspection.getId());
        assertEquals("Test Inspector", foundInspection.getInspector());
    }

    @Test
    void testFindById_NonExistentInspection_ReturnsEmpty() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();

        // Act
        var result = inspectionRepository.findById(nonExistentId);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void testUpdateInspection_UpdatesFields() {
        // Arrange
        Inspection inspection = new Inspection();
        inspection.setHive(testHive);
        inspection.setInspector("Original Inspector");
        inspection.setDate(LocalDate.of(2024, 1, 1));
        inspection.setNotes("Original notes");
        Inspection savedInspection = entityManager.persistAndFlush(inspection);

        // Act - Update the inspection
        savedInspection.setInspector("Updated Inspector");
        savedInspection.setDate(LocalDate.of(2024, 2, 1));
        savedInspection.setNotes("Updated notes");
        Inspection updatedInspection = inspectionRepository.save(savedInspection);
        entityManager.flush();
        entityManager.clear();

        // Assert
        Inspection foundInspection = entityManager.find(Inspection.class, updatedInspection.getId());
        assertNotNull(foundInspection);
        assertEquals("Updated Inspector", foundInspection.getInspector());
        assertEquals(LocalDate.of(2024, 2, 1), foundInspection.getDate());
        assertEquals("Updated notes", foundInspection.getNotes());
    }

    @Test
    void testDeleteInspection_RemovesFromDatabase() {
        // Arrange
        Inspection inspection = new Inspection();
        inspection.setHive(testHive);
        inspection.setInspector("To Be Deleted");
        inspection.setDate(LocalDate.of(2024, 1, 1));
        Inspection savedInspection = entityManager.persistAndFlush(inspection);
        UUID inspectionId = savedInspection.getId();

        // Act
        inspectionRepository.delete(savedInspection);
        entityManager.flush();
        entityManager.clear();

        // Assert
        Inspection foundInspection = entityManager.find(Inspection.class, inspectionId);
        assertNull(foundInspection);
    }

    @Test
    void testInspectionHiveRelationship_IsMaintained() {
        // Arrange
        Inspection inspection = new Inspection();
        inspection.setHive(testHive);
        inspection.setInspector("Relationship Test");
        inspection.setDate(LocalDate.of(2024, 1, 1));
        Inspection savedInspection = entityManager.persistAndFlush(inspection);
        UUID inspectionId = savedInspection.getId();
        entityManager.clear();

        // Act
        Inspection foundInspection = entityManager.find(Inspection.class, inspectionId);

        // Assert
        assertNotNull(foundInspection);
        assertNotNull(foundInspection.getHive());
        assertEquals(testHive.getId(), foundInspection.getHive().getId());
    }
}

