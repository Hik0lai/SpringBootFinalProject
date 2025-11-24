package com.beehivemonitor.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "sensor_readings")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SensorReading {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sensor_id", nullable = false)
    private Sensor sensor;
    
    @Column(nullable = false)
    private String type;
    
    @NotNull
    @Column(name = "reading_value", nullable = false)
    private Double value;
    
    @Column(nullable = false)
    private String unit;
    
    @Column(nullable = false)
    private LocalDateTime timestamp = LocalDateTime.now();
}


