package com.beehivemonitor.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "sensor_readings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"sensor"})
public class SensorReading {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
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


