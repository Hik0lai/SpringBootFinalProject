package com.beehivemonitor.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "alerts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"hive"})
public class Alert {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
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
}


