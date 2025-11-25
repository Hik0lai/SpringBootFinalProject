package com.beehivemonitor.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "hives")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Hive {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private String location;
    
    private String queen;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties({"hives", "password"})
    private User user;
    
    @OneToMany(mappedBy = "hive", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties({"hive"})
    private List<Sensor> sensors = new ArrayList<>();
    
    @OneToMany(mappedBy = "hive", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties({"hive"})
    private List<Inspection> inspections = new ArrayList<>();
    
    @OneToMany(mappedBy = "hive", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties({"hive"})
    private List<Alert> alerts = new ArrayList<>();
}


