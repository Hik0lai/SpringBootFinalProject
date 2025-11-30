package com.beehivemonitor.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "hive_sensor_data")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HiveSensorData {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hive_id", nullable = false)
    @JsonIgnoreProperties({"sensors", "user", "inspections", "alerts"})
    private Hive hive;
    
    @Column(nullable = false)
    private Double temperature;
    
    @Column(nullable = false)
    private Double humidity;
    
    @Column(nullable = false)
    private Double co2;
    
    @Column(nullable = false)
    private Double soundLevel;
    
    @Column(nullable = false)
    private Double weight;
    
    @Column(nullable = false)
    private LocalDateTime timestamp = LocalDateTime.now();
}

