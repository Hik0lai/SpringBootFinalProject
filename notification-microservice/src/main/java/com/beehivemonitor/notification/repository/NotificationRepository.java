package com.beehivemonitor.notification.repository;

import com.beehivemonitor.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByRecipientEmailOrderByCreatedAtDesc(String email);
    List<Notification> findByAlertId(Long alertId);
    List<Notification> findByStatus(Notification.NotificationStatus status);
}

