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

    public List<Hive> getAllHivesByUser(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
        return hiveRepository.findByUser(user);
    }

    public Hive getHiveById(Long id, String email) {
        Hive hive = hiveRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Hive not found"));
        
        // Verify ownership
        if (!hive.getUser().getEmail().equals(email)) {
            throw new RuntimeException("Unauthorized access to hive");
        }
        
        return hive;
    }

    @Transactional
    public Hive createHive(Hive hive, String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
        hive.setUser(user);
        return hiveRepository.save(hive);
    }

    @Transactional
    public Hive updateHive(Long id, Hive updatedHive, String email) {
        Hive hive = getHiveById(id, email);
        hive.setName(updatedHive.getName());
        hive.setLocation(updatedHive.getLocation());
        hive.setQueen(updatedHive.getQueen());
        return hiveRepository.save(hive);
    }

    @Transactional
    public void deleteHive(Long id, String email) {
        Hive hive = getHiveById(id, email);
        hiveRepository.delete(hive);
    }
}

