package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

 // Yeh add karo class mein:
    @Autowired
    private EmailService emailService;

    // register() method mein save ke baad:
    public String register(String name, String email, String password) {
        if (userRepository.existsByEmail(email)) return "EXISTS";
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
        
        // ✅ Welcome email bhejo
        emailService.sendWelcomeEmail(email, name);
        
        return "SUCCESS";
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    public void activateTrial(String email) {
        User user = findByEmail(email);
        if (user != null && !user.isTrialActivated()) {
            user.setTrialActivated(true);
            user.setTrialStartedAt(LocalDateTime.now());
            userRepository.save(user);
        }
    }

    public String getTrialStatus(String email) {
        User user = findByEmail(email);
        if (user == null) return "NOT_STARTED";
        if (!user.isTrialActivated()) return "NOT_STARTED";
        long hours = ChronoUnit.HOURS.between(
            user.getTrialStartedAt(), LocalDateTime.now()
        );
        return hours < 48 ? "ACTIVE" : "EXPIRED";
    }

    public long getTrialRemainingMs(String email) {
        User user = findByEmail(email);
        if (user == null || !user.isTrialActivated()) return 48L * 60 * 60 * 1000;
        long elapsed = ChronoUnit.MILLIS.between(
            user.getTrialStartedAt(), LocalDateTime.now()
        );
        return Math.max(0, 48L * 60 * 60 * 1000 - elapsed);
    }
    
    
}