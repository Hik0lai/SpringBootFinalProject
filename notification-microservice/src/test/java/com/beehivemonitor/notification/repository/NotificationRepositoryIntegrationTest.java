package com.beehivemonitor.notification.repository;

import com.beehivemonitor.notification.entity.Notification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration Test for NotificationRepository
 * Tests repository methods with actual database operations using H2 in-memory database
 */
@DataJpaTest
@ActiveProfiles("test")
class NotificationRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private NotificationRepository notificationRepository;

    private Notification testNotification;

    @BeforeEach
    void setUp() {
        testNotification = new Notification();
        testNotification.setRecipientEmail("test@example.com");
        testNotification.setSubject("Test Subject");
        testNotification.setMessage("Test Message");
        testNotification.setChannel(Notification.NotificationChannel.EMAIL);
        testNotification.setStatus(Notification.NotificationStatus.PENDING);
        testNotification.setAlertId(1L);
        testNotification.setCreatedAt(LocalDateTime.now());
        testNotification = entityManager.persistAndFlush(testNotification);
    }

    @Test
    void testFindAllNotifications() {
        // Arrange - Create another notification
        Notification notification2 = new Notification();
        notification2.setRecipientEmail("test2@example.com");
        notification2.setSubject("Test Subject 2");
        notification2.setMessage("Test Message 2");
        notification2.setChannel(Notification.NotificationChannel.EMAIL);
        notification2.setStatus(Notification.NotificationStatus.SENT);
        notification2.setAlertId(2L);
        notification2.setCreatedAt(LocalDateTime.now());
        entityManager.persistAndFlush(notification2);

        // Act
        List<Notification> notifications = notificationRepository.findAll();

        // Assert
        assertNotNull(notifications);
        assertTrue(notifications.size() >= 2);
        assertTrue(notifications.stream().anyMatch(n -> n.getRecipientEmail().equals("test@example.com")));
        assertTrue(notifications.stream().anyMatch(n -> n.getRecipientEmail().equals("test2@example.com")));
    }

    @Test
    void testFindById() {
        // Act
        Optional<Notification> found = notificationRepository.findById(testNotification.getId());

        // Assert
        assertTrue(found.isPresent());
        assertEquals("test@example.com", found.get().getRecipientEmail());
        assertEquals("Test Subject", found.get().getSubject());
        assertEquals(Notification.NotificationStatus.PENDING, found.get().getStatus());
    }

    @Test
    void testFindById_NotFound() {
        // Arrange
        Long nonExistentId = 9999L;

        // Act
        Optional<Notification> found = notificationRepository.findById(nonExistentId);

        // Assert
        assertFalse(found.isPresent());
    }

    @Test
    void testSaveNotification() {
        // Arrange
        Notification newNotification = new Notification();
        newNotification.setRecipientEmail("new@example.com");
        newNotification.setSubject("New Subject");
        newNotification.setMessage("New Message");
        newNotification.setChannel(Notification.NotificationChannel.EMAIL);
        newNotification.setStatus(Notification.NotificationStatus.PENDING);
        newNotification.setCreatedAt(LocalDateTime.now());

        // Act
        Notification saved = notificationRepository.save(newNotification);
        entityManager.flush();
        entityManager.clear();

        // Assert
        assertNotNull(saved.getId());
        Notification found = entityManager.find(Notification.class, saved.getId());
        assertNotNull(found);
        assertEquals("new@example.com", found.getRecipientEmail());
        assertEquals("New Subject", found.getSubject());
    }

    @Test
    void testFindByRecipientEmailOrderByCreatedAtDesc() {
        // Arrange - Create multiple notifications for same recipient
        Notification notification1 = new Notification();
        notification1.setRecipientEmail("recipient@example.com");
        notification1.setSubject("Subject 1");
        notification1.setMessage("Message 1");
        notification1.setChannel(Notification.NotificationChannel.EMAIL);
        notification1.setStatus(Notification.NotificationStatus.PENDING);
        notification1.setCreatedAt(LocalDateTime.now().minusHours(2));
        entityManager.persistAndFlush(notification1);

        Notification notification2 = new Notification();
        notification2.setRecipientEmail("recipient@example.com");
        notification2.setSubject("Subject 2");
        notification2.setMessage("Message 2");
        notification2.setChannel(Notification.NotificationChannel.EMAIL);
        notification2.setStatus(Notification.NotificationStatus.SENT);
        notification2.setCreatedAt(LocalDateTime.now().minusHours(1));
        entityManager.persistAndFlush(notification2);

        Notification notification3 = new Notification();
        notification3.setRecipientEmail("recipient@example.com");
        notification3.setSubject("Subject 3");
        notification3.setMessage("Message 3");
        notification3.setChannel(Notification.NotificationChannel.EMAIL);
        notification3.setStatus(Notification.NotificationStatus.SENT);
        notification3.setCreatedAt(LocalDateTime.now());
        entityManager.persistAndFlush(notification3);

        // Act
        List<Notification> notifications = notificationRepository.findByRecipientEmailOrderByCreatedAtDesc("recipient@example.com");

        // Assert
        assertNotNull(notifications);
        assertEquals(3, notifications.size());
        // Verify ordering (most recent first)
        assertTrue(notifications.get(0).getCreatedAt().isAfter(notifications.get(1).getCreatedAt()) ||
                   notifications.get(0).getCreatedAt().equals(notifications.get(1).getCreatedAt()));
        assertTrue(notifications.get(1).getCreatedAt().isAfter(notifications.get(2).getCreatedAt()) ||
                   notifications.get(1).getCreatedAt().equals(notifications.get(2).getCreatedAt()));
    }

    @Test
    void testFindByAlertId() {
        // Arrange - Create multiple notifications for same alert
        Long alertId = 100L;
        
        Notification notification1 = new Notification();
        notification1.setRecipientEmail("user1@example.com");
        notification1.setSubject("Alert 1");
        notification1.setMessage("Message 1");
        notification1.setChannel(Notification.NotificationChannel.EMAIL);
        notification1.setStatus(Notification.NotificationStatus.SENT);
        notification1.setAlertId(alertId);
        notification1.setCreatedAt(LocalDateTime.now());
        entityManager.persistAndFlush(notification1);

        Notification notification2 = new Notification();
        notification2.setRecipientEmail("user2@example.com");
        notification2.setSubject("Alert 2");
        notification2.setMessage("Message 2");
        notification2.setChannel(Notification.NotificationChannel.EMAIL);
        notification2.setStatus(Notification.NotificationStatus.SENT);
        notification2.setAlertId(alertId);
        notification2.setCreatedAt(LocalDateTime.now());
        entityManager.persistAndFlush(notification2);

        // Act
        List<Notification> notifications = notificationRepository.findByAlertId(alertId);

        // Assert
        assertNotNull(notifications);
        assertEquals(2, notifications.size());
        assertTrue(notifications.stream().allMatch(n -> n.getAlertId().equals(alertId)));
    }

    @Test
    void testFindByStatus() {
        // Arrange - Create notifications with different statuses
        Notification pendingNotification = new Notification();
        pendingNotification.setRecipientEmail("pending@example.com");
        pendingNotification.setSubject("Pending");
        pendingNotification.setMessage("Pending message");
        pendingNotification.setChannel(Notification.NotificationChannel.EMAIL);
        pendingNotification.setStatus(Notification.NotificationStatus.PENDING);
        pendingNotification.setCreatedAt(LocalDateTime.now());
        entityManager.persistAndFlush(pendingNotification);

        Notification sentNotification = new Notification();
        sentNotification.setRecipientEmail("sent@example.com");
        sentNotification.setSubject("Sent");
        sentNotification.setMessage("Sent message");
        sentNotification.setChannel(Notification.NotificationChannel.EMAIL);
        sentNotification.setStatus(Notification.NotificationStatus.SENT);
        sentNotification.setSentAt(LocalDateTime.now());
        sentNotification.setCreatedAt(LocalDateTime.now());
        entityManager.persistAndFlush(sentNotification);

        Notification failedNotification = new Notification();
        failedNotification.setRecipientEmail("failed@example.com");
        failedNotification.setSubject("Failed");
        failedNotification.setMessage("Failed message");
        failedNotification.setChannel(Notification.NotificationChannel.EMAIL);
        failedNotification.setStatus(Notification.NotificationStatus.FAILED);
        failedNotification.setErrorMessage("Error occurred");
        failedNotification.setCreatedAt(LocalDateTime.now());
        entityManager.persistAndFlush(failedNotification);

        // Act
        List<Notification> pendingNotifications = notificationRepository.findByStatus(Notification.NotificationStatus.PENDING);
        List<Notification> sentNotifications = notificationRepository.findByStatus(Notification.NotificationStatus.SENT);
        List<Notification> failedNotifications = notificationRepository.findByStatus(Notification.NotificationStatus.FAILED);

        // Assert
        assertNotNull(pendingNotifications);
        assertTrue(pendingNotifications.stream().anyMatch(n -> n.getRecipientEmail().equals("pending@example.com")));
        
        assertNotNull(sentNotifications);
        assertTrue(sentNotifications.stream().anyMatch(n -> n.getRecipientEmail().equals("sent@example.com")));
        
        assertNotNull(failedNotifications);
        assertTrue(failedNotifications.stream().anyMatch(n -> n.getRecipientEmail().equals("failed@example.com")));
    }

    @Test
    void testUpdateNotification() {
        // Arrange
        testNotification.setStatus(Notification.NotificationStatus.SENT);
        testNotification.setSentAt(LocalDateTime.now());

        // Act
        Notification updated = notificationRepository.save(testNotification);
        entityManager.flush();
        entityManager.clear();

        // Assert
        Notification found = entityManager.find(Notification.class, updated.getId());
        assertNotNull(found);
        assertEquals(Notification.NotificationStatus.SENT, found.getStatus());
        assertNotNull(found.getSentAt());
    }

    @Test
    void testDeleteNotification() {
        // Arrange
        Long notificationId = testNotification.getId();

        // Act
        notificationRepository.deleteById(notificationId);
        entityManager.flush();
        entityManager.clear();

        // Assert
        Notification found = entityManager.find(Notification.class, notificationId);
        assertNull(found);
    }
}

