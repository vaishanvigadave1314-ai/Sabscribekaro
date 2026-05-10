package com.example.demo.controller;

import com.example.demo.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    // Home
    @GetMapping("/")
    public String home() { return "index"; }

    // Register Page
    @GetMapping("/register")
    public String registerPage() { return "register"; }

    // Register Submit
    @PostMapping("/register")
    public String registerSubmit(@RequestParam String name,
                                  @RequestParam String email,
                                  @RequestParam String password,
                                  @RequestParam String confirmPassword,
                                  Model model) {
        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match!");
            return "register";
        }
        if (password.length() < 6) {
            model.addAttribute("error", "Password must be at least 6 characters!");
            return "register";
        }
        String result = userService.register(name, email, password);
        if (result.equals("EXISTS")) {
            model.addAttribute("error", "Email already registered! Please login.");
            return "register";
        }
        return "redirect:/login?registered=true";
    }

    // Login Page
    @GetMapping("/login")
    public String loginPage(@RequestParam(required = false) String registered,
                             @RequestParam(required = false) String error,
                             @RequestParam(required = false) String logout,
                             Model model) {
        if (registered != null) model.addAttribute("success", "✅ Account created! Please login.");
        if (error != null)      model.addAttribute("error", "❌ Invalid email or password!");
        if (logout != null)     model.addAttribute("success", "👋 Logged out successfully.");
        return "login";
    }
 // Yeh add karo AuthController.java mein:
    @GetMapping("/pricing")
    public String pricingPage() { 
        return "pricing"; 
    }
   
}