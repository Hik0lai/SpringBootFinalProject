package com.beehivemonitor.service;

import com.beehivemonitor.entity.Alert;
import com.beehivemonitor.entity.User;
import com.beehivemonitor.repository.AlertRepository;
import com.beehivemonitor.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AlertService {

    @Autowired
    private AlertRepository alertRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Alert> getAllAlertsByUser(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
        return alertRepository.findByUserId(user.getId());
    }
}

