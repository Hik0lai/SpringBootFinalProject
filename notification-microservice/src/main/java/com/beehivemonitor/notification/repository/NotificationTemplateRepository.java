package com.beehivemonitor.notification.repository;

import com.beehivemonitor.notification.entity.Notification;
import com.beehivemonitor.notification.entity.NotificationTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NotificationTemplateRepository extends JpaRepository<NotificationTemplate, Long> {
    Optional<NotificationTemplate> findByNameAndIsActiveTrue(String name);
    Optional<NotificationTemplate> findByNameAndChannelAndIsActiveTrue(String name, Notification.NotificationChannel channel);
}

