package com.example.demo.service;

import com.example.demo.model.Website;
import com.example.demo.repository.WebsiteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;
import java.nio.file.*;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
public class WebsiteService {

    @Autowired
    private WebsiteRepository websiteRepository;

    // ✅ FIX 1 — dynamic base dir from env
    private String getBaseDir() {
        String dir = System.getenv("UPLOAD_DIR");
        return (dir != null && !dir.isEmpty()) ? dir : "uploads/";
    }

    // ✅ FIX 2 — dynamic base URL from env
    private String getBaseUrl() {
        String base = System.getenv("BASE_URL");
        return (base != null && !base.isEmpty()) ? base : "http://localhost:8080";
    }

    public String uploadWebsite(String userEmail, String siteName,
                                 MultipartFile zipFile) throws IOException {

        String safeName = siteName.replaceAll("[^a-zA-Z0-9_-]", "_");

        // ✅ FIX 3 — use getBaseDir() instead of BASE_DIR
        Path siteDir = Paths.get(getBaseDir(), userEmail, safeName);

        if (Files.exists(siteDir)) return "EXISTS";
        Files.createDirectories(siteDir);

        try (ZipInputStream zis = new ZipInputStream(zipFile.getInputStream())) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                Path entryPath = siteDir.resolve(entry.getName()).normalize();
                if (!entryPath.startsWith(siteDir)) {
                    throw new IOException("Invalid ZIP entry: " + entry.getName());
                }
                if (entry.isDirectory()) {
                    Files.createDirectories(entryPath);
                } else {
                    Files.createDirectories(entryPath.getParent());
                    Files.copy(zis, entryPath, StandardCopyOption.REPLACE_EXISTING);
                }
                zis.closeEntry();
            }
        }

        Website website = new Website();
        website.setName(safeName);
        website.setOwnerEmail(userEmail);
        website.setFolderPath(siteDir.toString());

        // ✅ FIX 4 — full public URL with domain
        String encodedEmail = userEmail
                .replace("@", "_at_")
                .replace(".", "_dot_");
        website.setSiteUrl(getBaseUrl() + "/site/" + encodedEmail + "/" + safeName);

        websiteRepository.save(website);
        return "SUCCESS";
    }

    public List<Website> getUserWebsites(String userEmail) {
        return websiteRepository.findByOwnerEmail(userEmail);
    }

    public List<String> listFiles(Long websiteId, String userEmail) throws IOException {
        Website site = websiteRepository.findById(websiteId).orElse(null);
        if (site == null || !site.getOwnerEmail().equals(userEmail)) return List.of();
        Path siteDir = Paths.get(site.getFolderPath());
        List<String> files = new java.util.ArrayList<>();
        Files.walk(siteDir)
             .filter(Files::isRegularFile)
             .forEach(p -> files.add(siteDir.relativize(p).toString()));
        return files;
    }

    public boolean deleteFile(Long websiteId, String fileName,
                               String userEmail) throws IOException {
        Website site = websiteRepository.findById(websiteId).orElse(null);
        if (site == null || !site.getOwnerEmail().equals(userEmail)) return false;
        Path filePath = Paths.get(site.getFolderPath(), fileName).normalize();
        if (!filePath.startsWith(Paths.get(site.getFolderPath()))) return false;
        Files.deleteIfExists(filePath);
        return true;
    }

    public boolean deleteWebsite(Long websiteId, String userEmail) throws IOException {
        Website site = websiteRepository.findById(websiteId).orElse(null);
        if (site == null || !site.getOwnerEmail().equals(userEmail)) return false;
        Path dir = Paths.get(site.getFolderPath());
        if (Files.exists(dir)) {
            Files.walk(dir)
                 .sorted(java.util.Comparator.reverseOrder())
                 .map(Path::toFile)
                 .forEach(File::delete);
        }
        websiteRepository.delete(site);
        return true;
    }
}