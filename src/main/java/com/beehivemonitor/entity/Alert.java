package com.beehivemonitor.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "alerts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Alert {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Column(nullable = false, columnDefinition = "VARCHAR(255) DEFAULT 'Alert'")
    private String name; // Alert name/description
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hive_id", nullable = false)
    @JsonIgnoreProperties({"sensors", "user", "inspections", "alerts"})
    private Hive hive;
    
    @Column(name = "trigger_conditions", columnDefinition = "TEXT")
    private String triggerConditions; // JSON string: [{"parameter":"temperature","operator":">","value":25},...]
    
    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "is_triggered")
    private Boolean isTriggered = false; // Whether alert is currently triggered
    
    // Legacy field - keeping temporarily for database migration
    // TODO: Remove after running update-alerts-table.sql
    @Column(nullable = true, insertable = false, updatable = false)
    private String message;
    
    @Column(nullable = true, insertable = false, updatable = false)
    private String title;
}


