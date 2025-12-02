package com.beehivemonitor.service;

import com.beehivemonitor.entity.Hive;
import com.beehivemonitor.entity.User;
import com.beehivemonitor.repository.HiveRepository;
import com.beehivemonitor.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class HiveService {

    @Autowired
    private HiveRepository hiveRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Hive> getAllHives() {
        // Return all hives - all users can view all hives
        return hiveRepository.findAll();
    }

    public Hive getHiveById(Long id) {
        // All users can view any hive - no ownership check needed
        return hiveRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Hive not found"));
    }

    @Transactional
    public Hive createHive(Hive hive, String email, User.Role userRole) {
        // Only admins can create hives
        if (userRole != User.Role.ADMIN) {
            throw new RuntimeException("Only administrators can create hives");
        }
        
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
        hive.setUser(user);
        return hiveRepository.save(hive);
    }

    @Transactional
    public Hive updateHive(Long id, Hive updatedHive, String email, User.Role userRole) {
        // Only admins can update hives
        if (userRole != User.Role.ADMIN) {
            throw new RuntimeException("Only administrators can update hives");
        }
        
        Hive hive = getHiveById(id);
        hive.setName(updatedHive.getName());
        hive.setLocation(updatedHive.getLocation());
        hive.setBirthDate(updatedHive.getBirthDate());
        return hiveRepository.save(hive);
    }

    @Transactional
    public void deleteHive(Long id, String email, User.Role userRole) {
        // Only admins can delete hives
        if (userRole != User.Role.ADMIN) {
            throw new RuntimeException("Only administrators can delete hives");
        }
        
        Hive hive = getHiveById(id);
        hiveRepository.delete(hive);
    }
}

