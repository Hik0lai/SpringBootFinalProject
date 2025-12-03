package com.beehivemonitor.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "hive_sensor_data")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"hive"})
public class HiveSensorData {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hive_id", nullable = false)
    @JsonIgnoreProperties({"sensors", "user", "inspections", "alerts"})
    private Hive hive;
    
    @Column(nullable = false)
    private Double temperature; // Internal temperature
    
    @Column(nullable = false)
    private Double externalTemperature; // External temperature
    
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

