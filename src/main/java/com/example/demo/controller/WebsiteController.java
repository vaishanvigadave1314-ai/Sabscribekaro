package com.example.demo.controller;

import com.example.demo.model.Website;
import com.example.demo.repository.WebsiteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.*;

@Controller
public class WebsiteController {

    @Autowired
    private WebsiteRepository websiteRepository;

    // Root — serves index.html
    @GetMapping("/site/{encodedEmail}/{siteName}")
    public ResponseEntity<Resource> serveRoot(
            @PathVariable String encodedEmail,
            @PathVariable String siteName) throws IOException {

        String realEmail = decodeEmail(encodedEmail);
        Website site = websiteRepository
                .findByOwnerEmailAndName(realEmail, siteName)
                .orElse(null);

        if (site == null) return ResponseEntity.notFound().build();

        Path baseDir = Paths.get(site.getFolderPath()).normalize();

        // Try common entry files
        String[] entries = {"index.html", "home.html", "index.htm", "default.html"};
        for (String entry : entries) {
            Path p = baseDir.resolve(entry);
            if (Files.exists(p) && Files.isRegularFile(p)) {
                return serveFile(p);
            }
        }

        // Try to find index.html recursively (ZIP had a subfolder)
        Path found = Files.walk(baseDir)
                .filter(p -> p.getFileName().toString().equals("index.html"))
                .findFirst().orElse(null);

        if (found == null) return ResponseEntity.notFound().build();
        return serveFile(found);
    }

    // All static assets — handles unlimited nesting via **
    @GetMapping("/site/{encodedEmail}/{siteName}/**")
    public ResponseEntity<Resource> serveAsset(
            @PathVariable String encodedEmail,
            @PathVariable String siteName,
            jakarta.servlet.http.HttpServletRequest request) throws IOException {

        String realEmail = decodeEmail(encodedEmail);
        Website site = websiteRepository
                .findByOwnerEmailAndName(realEmail, siteName)
                .orElse(null);

        if (site == null) return ResponseEntity.notFound().build();

        String prefix = "/site/" + encodedEmail + "/" + siteName + "/";
        String relativePath = request.getRequestURI().substring(prefix.length());

        if (relativePath.isEmpty()) {
            return serveRoot(encodedEmail, siteName);
        }

        Path baseDir = Paths.get(site.getFolderPath()).normalize();
        Path filePath = baseDir.resolve(relativePath).normalize();

        // Security: prevent path traversal
        if (!filePath.startsWith(baseDir)) {
            return ResponseEntity.status(403).build();
        }

        // If directory, serve its index.html
        if (Files.isDirectory(filePath)) {
            filePath = filePath.resolve("index.html");
        }

        if (!Files.exists(filePath)) return ResponseEntity.notFound().build();

        return serveFile(filePath);
    }

    // ── Helpers ──

    private String decodeEmail(String encoded) {
        return encoded.replace("_at_", "@").replace("_dot_", ".");
    }

    private ResponseEntity<Resource> serveFile(Path filePath) throws IOException {
        Resource resource = new UrlResource(filePath.toUri());
        String contentType = Files.probeContentType(filePath);
        if (contentType == null) {
            String name = filePath.getFileName().toString().toLowerCase();
            if (name.endsWith(".css"))        contentType = "text/css";
            else if (name.endsWith(".js"))    contentType = "application/javascript";
            else if (name.endsWith(".html"))  contentType = "text/html";
            else if (name.endsWith(".png"))   contentType = "image/png";
            else if (name.endsWith(".jpg") || name.endsWith(".jpeg")) contentType = "image/jpeg";
            else if (name.endsWith(".svg"))   contentType = "image/svg+xml";
            else if (name.endsWith(".ico"))   contentType = "image/x-icon";
            else if (name.endsWith(".woff2")) contentType = "font/woff2";
            else if (name.endsWith(".woff"))  contentType = "font/woff";
            else if (name.endsWith(".ttf"))   contentType = "font/ttf";
            else if (name.endsWith(".json"))  contentType = "application/json";
            else contentType = "application/octet-stream";
        }
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(resource);
    }
}