package com.example.demo.repository;

import com.example.demo.model.Website;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface WebsiteRepository extends JpaRepository<Website, Long> {
    List<Website> findByOwnerEmail(String ownerEmail);
    Optional<Website> findByOwnerEmailAndName(String ownerEmail, String name); // ADD THIS
}