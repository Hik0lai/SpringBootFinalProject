package com.beehivemonitor.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "hives")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"sensors", "inspections", "alerts", "user"})
public class Hive {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @NotBlank
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private String location;
    
    private String queen;
    
    @Column(name = "birth_date")
    private String birthDate; // Format: YYYY-MM (year-month)
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
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


