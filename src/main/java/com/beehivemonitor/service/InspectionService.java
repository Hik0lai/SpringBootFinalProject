package com.beehivemonitor.service;

import com.beehivemonitor.entity.Hive;
import com.beehivemonitor.entity.Inspection;
import com.beehivemonitor.entity.User;
import com.beehivemonitor.repository.HiveRepository;
import com.beehivemonitor.repository.InspectionRepository;
import com.beehivemonitor.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class InspectionService {

    @Autowired
    private InspectionRepository inspectionRepository;

    @Autowired
    private HiveRepository hiveRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Inspection> getAllInspectionsByUser(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
        return inspectionRepository.findByUserId(user.getId());
    }

    public Inspection getInspectionById(UUID id, String email) {
        Inspection inspection = inspectionRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Inspection not found"));
        
        // Verify ownership
        if (!inspection.getHive().getUser().getEmail().equals(email)) {
            throw new RuntimeException("Unauthorized access to inspection");
        }
        
        return inspection;
    }

    @Transactional
    public Inspection createInspection(Inspection inspection, String email) {
        Hive hive = hiveRepository.findById(inspection.getHive().getId())
            .orElseThrow(() -> new RuntimeException("Hive not found"));
        
        // Verify ownership
        if (!hive.getUser().getEmail().equals(email)) {
            throw new RuntimeException("Unauthorized access to hive");
        }
        
        inspection.setHive(hive);
        return inspectionRepository.save(inspection);
    }

    @Transactional
    public Inspection updateInspection(UUID id, Inspection updatedInspection, String email) {
        Inspection inspection = getInspectionById(id, email);
        inspection.setInspector(updatedInspection.getInspector());
        inspection.setDate(updatedInspection.getDate());
        inspection.setNotes(updatedInspection.getNotes());
        return inspectionRepository.save(inspection);
    }

    @Transactional
    public void deleteInspection(UUID id, String email) {
        Inspection inspection = getInspectionById(id, email); // This also verifies ownership
        inspectionRepository.delete(inspection);
    }
}

