package com.example.demo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "websites")
public class Website {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String ownerEmail;

    @Column(nullable = false)
    private String folderPath;

    @Column(nullable = false)
    private String status = "SAVED";

    private LocalDateTime uploadedAt = LocalDateTime.now();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getOwnerEmail() { return ownerEmail; }
    public void setOwnerEmail(String o) { this.ownerEmail = o; }
    public String getFolderPath() { return folderPath; }
    public void setFolderPath(String f) { this.folderPath = f; }
    public String getStatus() { return status; }
    public void setStatus(String s) { this.status = s; }
    public LocalDateTime getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(LocalDateTime t) { this.uploadedAt = t; }
 // Existing fields ke baad yeh add karo
    @Column
    private String siteUrl;

    // Getter & Setter
    public String getSiteUrl() { return siteUrl; }
    public void setSiteUrl(String siteUrl) { this.siteUrl = siteUrl; }

}