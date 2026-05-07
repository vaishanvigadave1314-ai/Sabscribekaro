package com.example.demo.controller;

import com.example.demo.model.Website;
import com.example.demo.service.UserService;
import com.example.demo.service.WebsiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class DashboardController {

    @Autowired
    private WebsiteService websiteService;

    @Autowired
    private UserService userService;

    @GetMapping("/dashboard")
    public String dashboard(Authentication auth, Model model) {
        String email = auth.getName();
        List<Website> sites = websiteService.getUserWebsites(email);
        String trialStatus = userService.getTrialStatus(email);
        long remainingMs = userService.getTrialRemainingMs(email);
        model.addAttribute("name", email);
        model.addAttribute("websites", sites);
        model.addAttribute("trialStatus", trialStatus);
        model.addAttribute("remainingMs", remainingMs);
        return "dashboard";
    }

    @PostMapping("/trial/activate")
    @ResponseBody
    public String activateTrial(Authentication auth) {
        userService.activateTrial(auth.getName());
        return "OK";
    }

    @PostMapping("/upload")
    public String uploadSite(@RequestParam("siteName") String siteName,
                              @RequestParam("zipFile") MultipartFile zipFile,
                              Authentication auth,
                              RedirectAttributes ra) {
        String status = userService.getTrialStatus(auth.getName());
        if (status.equals("EXPIRED")) {
            ra.addFlashAttribute("error", "Your trial has expired! Please upgrade.");
            return "redirect:/dashboard";
        }
        try {
            if (zipFile.isEmpty()) {
                ra.addFlashAttribute("error", "Please select a ZIP file!");
                return "redirect:/dashboard";
            }
            if (!zipFile.getOriginalFilename().endsWith(".zip")) {
                ra.addFlashAttribute("error", "Only .zip files are allowed!");
                return "redirect:/dashboard";
            }
            String result = websiteService.uploadWebsite(
                auth.getName(), siteName, zipFile
            );
            if (result.equals("EXISTS")) {
                ra.addFlashAttribute("error", "A website with this name already exists!");
            } else {
                ra.addFlashAttribute("success", "✅ Website uploaded successfully: " + siteName);
            }
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Upload failed: " + e.getMessage());
        }
        return "redirect:/dashboard";
    }

    @GetMapping("/files/{websiteId}")
    public String fileManager(@PathVariable Long websiteId,
                               Authentication auth, Model model) {
        try {
            List<String> files = websiteService.listFiles(websiteId, auth.getName());
            model.addAttribute("files", files);
            model.addAttribute("websiteId", websiteId);
        } catch (Exception e) {
            model.addAttribute("error", "Failed to load files!");
        }
        return "filemanager";
    }

    @PostMapping("/files/{websiteId}/delete")
    public String deleteFile(@PathVariable Long websiteId,
                              @RequestParam String fileName,
                              Authentication auth,
                              RedirectAttributes ra) {
        try {
            websiteService.deleteFile(websiteId, fileName, auth.getName());
            ra.addFlashAttribute("success", "File deleted successfully!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Delete failed!");
        }
        return "redirect:/files/" + websiteId;
    }

    @PostMapping("/website/{websiteId}/delete")
    public String deleteWebsite(@PathVariable Long websiteId,
                                 Authentication auth,
                                 RedirectAttributes ra) {
        try {
            websiteService.deleteWebsite(websiteId, auth.getName());
            ra.addFlashAttribute("success", "Website deleted successfully!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Delete failed!");
        }
        return "redirect:/dashboard";
    }
}