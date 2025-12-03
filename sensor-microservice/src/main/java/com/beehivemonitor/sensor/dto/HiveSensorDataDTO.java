package com.beehivemonitor.sensor.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HiveSensorDataDTO {
    private double temperature;
    private double externalTemperature;
    private double humidity;
    private double co2;
    private double soundLevel;
    private double weight;
}


