package com.beehivemonitor.notification.repository;

import com.beehivemonitor.notification.entity.Notification;
import com.beehivemonitor.notification.entity.NotificationTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration Test for NotificationTemplateRepository
 * Tests repository methods with actual database operations using H2 in-memory database
 */
@DataJpaTest
@ActiveProfiles("test")
class NotificationTemplateRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private NotificationTemplateRepository templateRepository;

    private NotificationTemplate testTemplate;

    @BeforeEach
    void setUp() {
        testTemplate = new NotificationTemplate();
        testTemplate.setName("ALERT_TRIGGERED");
        testTemplate.setChannel(Notification.NotificationChannel.EMAIL);
        testTemplate.setSubjectTemplate("Alert: {{alertName}}");
        testTemplate.setBodyTemplate("Alert {{alertName}} has been triggered for hive {{hiveName}}.");
        testTemplate.setIsActive(true);
        testTemplate = entityManager.persistAndFlush(testTemplate);
    }

    @Test
    void testFindAllTemplates() {
        // Arrange - Create another template
        NotificationTemplate template2 = new NotificationTemplate();
        template2.setName("ALERT_RESET");
        template2.setChannel(Notification.NotificationChannel.EMAIL);
        template2.setSubjectTemplate("Alert Reset: {{alertName}}");
        template2.setBodyTemplate("Alert {{alertName}} has been reset.");
        template2.setIsActive(true);
        entityManager.persistAndFlush(template2);

        // Act
        var templates = templateRepository.findAll();

        // Assert
        assertNotNull(templates);
        assertTrue(templates.size() >= 2);
        assertTrue(templates.stream().anyMatch(t -> t.getName().equals("ALERT_TRIGGERED")));
        assertTrue(templates.stream().anyMatch(t -> t.getName().equals("ALERT_RESET")));
    }

    @Test
    void testFindById() {
        // Act
        Optional<NotificationTemplate> found = templateRepository.findById(testTemplate.getId());

        // Assert
        assertTrue(found.isPresent());
        assertEquals("ALERT_TRIGGERED", found.get().getName());
        assertEquals(Notification.NotificationChannel.EMAIL, found.get().getChannel());
        assertTrue(found.get().getIsActive());
    }

    @Test
    void testFindById_NotFound() {
        // Arrange
        Long nonExistentId = 9999L;

        // Act
        Optional<NotificationTemplate> found = templateRepository.findById(nonExistentId);

        // Assert
        assertFalse(found.isPresent());
    }

    @Test
    void testSaveTemplate() {
        // Arrange
        NotificationTemplate newTemplate = new NotificationTemplate();
        newTemplate.setName("NEW_TEMPLATE");
        newTemplate.setChannel(Notification.NotificationChannel.EMAIL);
        newTemplate.setSubjectTemplate("New Subject");
        newTemplate.setBodyTemplate("New Body");
        newTemplate.setIsActive(true);

        // Act
        NotificationTemplate saved = templateRepository.save(newTemplate);
        entityManager.flush();
        entityManager.clear();

        // Assert
        assertNotNull(saved.getId());
        NotificationTemplate found = entityManager.find(NotificationTemplate.class, saved.getId());
        assertNotNull(found);
        assertEquals("NEW_TEMPLATE", found.getName());
        assertEquals("New Subject", found.getSubjectTemplate());
    }

    @Test
    void testFindByNameAndIsActiveTrue_ActiveTemplate() {
        // Act
        Optional<NotificationTemplate> found = templateRepository.findByNameAndIsActiveTrue("ALERT_TRIGGERED");

        // Assert
        assertTrue(found.isPresent());
        assertEquals("ALERT_TRIGGERED", found.get().getName());
        assertTrue(found.get().getIsActive());
    }

    @Test
    void testFindByNameAndIsActiveTrue_InactiveTemplate_ReturnsEmpty() {
        // Arrange - Create inactive template
        NotificationTemplate inactiveTemplate = new NotificationTemplate();
        inactiveTemplate.setName("INACTIVE_TEMPLATE");
        inactiveTemplate.setChannel(Notification.NotificationChannel.EMAIL);
        inactiveTemplate.setSubjectTemplate("Inactive Subject");
        inactiveTemplate.setBodyTemplate("Inactive Body");
        inactiveTemplate.setIsActive(false);
        entityManager.persistAndFlush(inactiveTemplate);

        // Act
        Optional<NotificationTemplate> found = templateRepository.findByNameAndIsActiveTrue("INACTIVE_TEMPLATE");

        // Assert
        assertFalse(found.isPresent());
    }

    @Test
    void testFindByNameAndChannelAndIsActiveTrue_Success() {
        // Act
        Optional<NotificationTemplate> found = templateRepository.findByNameAndChannelAndIsActiveTrue(
                "ALERT_TRIGGERED",
                Notification.NotificationChannel.EMAIL
        );

        // Assert
        assertTrue(found.isPresent());
        assertEquals("ALERT_TRIGGERED", found.get().getName());
        assertEquals(Notification.NotificationChannel.EMAIL, found.get().getChannel());
        assertTrue(found.get().getIsActive());
    }

    @Test
    void testFindByNameAndChannelAndIsActiveTrue_WrongChannel_ReturnsEmpty() {
        // Act
        Optional<NotificationTemplate> found = templateRepository.findByNameAndChannelAndIsActiveTrue(
                "ALERT_TRIGGERED",
                Notification.NotificationChannel.SMS
        );

        // Assert
        assertFalse(found.isPresent());
    }

    @Test
    void testFindByNameAndChannelAndIsActiveTrue_Inactive_ReturnsEmpty() {
        // Arrange - Deactivate the template
        testTemplate.setIsActive(false);
        entityManager.persistAndFlush(testTemplate);

        // Act
        Optional<NotificationTemplate> found = templateRepository.findByNameAndChannelAndIsActiveTrue(
                "ALERT_TRIGGERED",
                Notification.NotificationChannel.EMAIL
        );

        // Assert
        assertFalse(found.isPresent());
    }

    @Test
    void testUpdateTemplate() {
        // Arrange
        testTemplate.setSubjectTemplate("Updated Subject");
        testTemplate.setBodyTemplate("Updated Body");

        // Act
        NotificationTemplate updated = templateRepository.save(testTemplate);
        entityManager.flush();
        entityManager.clear();

        // Assert
        NotificationTemplate found = entityManager.find(NotificationTemplate.class, updated.getId());
        assertNotNull(found);
        assertEquals("Updated Subject", found.getSubjectTemplate());
        assertEquals("Updated Body", found.getBodyTemplate());
    }

    @Test
    void testDeleteTemplate() {
        // Arrange
        Long templateId = testTemplate.getId();

        // Act
        templateRepository.deleteById(templateId);
        entityManager.flush();
        entityManager.clear();

        // Assert
        NotificationTemplate found = entityManager.find(NotificationTemplate.class, templateId);
        assertNull(found);
    }

    @Test
    void testUniqueNameConstraint() {
        // Arrange - Try to create template with same name
        NotificationTemplate duplicateTemplate = new NotificationTemplate();
        duplicateTemplate.setName("ALERT_TRIGGERED"); // Same name as testTemplate
        duplicateTemplate.setChannel(Notification.NotificationChannel.SMS);
        duplicateTemplate.setSubjectTemplate("Duplicate Subject");
        duplicateTemplate.setBodyTemplate("Duplicate Body");
        duplicateTemplate.setIsActive(true);

        // Act & Assert - Should throw exception due to unique constraint on name field
        // The name field has unique = true, so duplicate names are not allowed
        assertThrows(org.springframework.dao.DataIntegrityViolationException.class, () -> {
            templateRepository.save(duplicateTemplate);
            entityManager.flush();
        });
    }

    @Test
    void testMultipleChannelsForSameName_ThrowsConstraintViolation() {
        // Arrange - Create template with unique name
        NotificationTemplate emailTemplate = new NotificationTemplate();
        emailTemplate.setName("MULTI_CHANNEL_EMAIL");
        emailTemplate.setChannel(Notification.NotificationChannel.EMAIL);
        emailTemplate.setSubjectTemplate("Email Subject");
        emailTemplate.setBodyTemplate("Email Body");
        emailTemplate.setIsActive(true);
        entityManager.persistAndFlush(emailTemplate);

        // Try to create another template with same name but different channel
        // This should fail because name field has unique constraint
        NotificationTemplate smsTemplate = new NotificationTemplate();
        smsTemplate.setName("MULTI_CHANNEL_EMAIL"); // Same name as emailTemplate
        smsTemplate.setChannel(Notification.NotificationChannel.SMS);
        smsTemplate.setSubjectTemplate("SMS Subject");
        smsTemplate.setBodyTemplate("SMS Body");
        smsTemplate.setIsActive(true);

        // Act & Assert - Should throw exception due to unique constraint violation
        assertThrows(org.springframework.dao.DataIntegrityViolationException.class, () -> {
            templateRepository.save(smsTemplate);
            entityManager.flush();
        });
    }
}

