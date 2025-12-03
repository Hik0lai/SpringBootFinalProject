package com.beehivemonitor.notification.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "notification_templates")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationTemplate {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String name; // e.g., "ALERT_TRIGGERED"
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Notification.NotificationChannel channel;
    
    @Column(nullable = false)
    private String subjectTemplate;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String bodyTemplate;
    
    @Column(nullable = false)
    private Boolean isActive = true;
}


