package com.example.demo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String role = "USER";

    // 🆕 Trial fields
    private LocalDateTime trialStartedAt;   // kab trial shuru hua
    private boolean trialActivated = false; // user ne popup dismiss kiya ya nahi

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    // 🆕
    public LocalDateTime getTrialStartedAt() { return trialStartedAt; }
    public void setTrialStartedAt(LocalDateTime t) { this.trialStartedAt = t; }
    public boolean isTrialActivated() { return trialActivated; }
    public void setTrialActivated(boolean t) { this.trialActivated = t; }
}