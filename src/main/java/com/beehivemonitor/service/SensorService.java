package com.beehivemonitor.service;

import com.beehivemonitor.dto.SensorReadingDTO;
import com.beehivemonitor.entity.Hive;
import com.beehivemonitor.repository.HiveRepository;
import com.beehivemonitor.repository.SensorReadingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SensorService {

    @Autowired
    private SensorReadingRepository sensorReadingRepository;

    @Autowired
    private HiveRepository hiveRepository;

    public List<SensorReadingDTO> getLatestReadingsByHiveId(Long hiveId, String email) {
        Hive hive = hiveRepository.findById(hiveId)
            .orElseThrow(() -> new RuntimeException("Hive not found"));
        
        // Verify ownership
        if (!hive.getUser().getEmail().equals(email)) {
            throw new RuntimeException("Unauthorized access to hive");
        }
        
        return sensorReadingRepository.findLatestReadingsByHiveId(hiveId).stream()
            .map(reading -> new SensorReadingDTO(reading.getType(), reading.getValue(), reading.getUnit()))
            .collect(Collectors.toList());
    }
}

