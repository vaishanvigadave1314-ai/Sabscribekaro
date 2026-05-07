package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendWelcomeEmail(String toEmail, String name) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("🎉 Welcome to Sabscribekaro!");
            helper.setFrom("sabscribekaroai@gmail.com");

            String html = """
                <!DOCTYPE html>
                <html>
                <body style="font-family:sans-serif;background:#04050f;color:#e8eaf6;padding:2rem;margin:0;">
                  <div style="max-width:500px;margin:0 auto;background:rgba(255,255,255,0.04);border:1px solid rgba(255,255,255,0.08);border-radius:24px;padding:2.5rem;">
                    <h1 style="font-size:1.5rem;color:#00e5ff;">Sabscribekaro 🚀</h1>
                    <h2 style="font-size:1.6rem;margin:1rem 0;">Welcome, %s! 🎉</h2>
                    <p style="color:#6e7a99;line-height:1.7;">
                      Thank you for choosing <strong style="color:#00e5ff;">Sabscribekaro</strong>!
                      Your account has been created successfully.
                    </p>
                    <div style="background:rgba(0,229,255,0.06);border:1px solid rgba(0,229,255,0.15);border-radius:16px;padding:1.5rem;margin:1.5rem 0;">
                      <p style="margin:0;font-size:0.95rem;color:#e8eaf6;">🎁 <strong>Your Free Trial Includes:</strong></p>
                      <ul style="color:#6e7a99;margin-top:0.8rem;line-height:2;">
                        <li>✅ 48-Hour Free Hosting</li>
                        <li>✅ Free SSL Certificate</li>
                        <li>✅ ZIP Upload Support</li>
                        <li>✅ File Manager</li>
                        <li>✅ Live Public URL</li>
                      </ul>
                    </div>
                    <a href="https://web-production-6a6fb.up.railway.app/dashboard"
                       style="display:inline-block;padding:0.9rem 2rem;border-radius:99px;background:linear-gradient(135deg,#7b2fff,#00e5ff);color:#fff;font-weight:700;text-decoration:none;">
                      Go to Dashboard →
                    </a>
                    <p style="color:#6e7a99;font-size:0.8rem;margin-top:2rem;">
                      If you didn't create this account, please ignore this email.
                    </p>
                  </div>
                </body>
                </html>
                """.formatted(name);

            helper.setText(html, true);
            mailSender.send(message);
            System.out.println("✅ Welcome email sent to: " + toEmail);

        } catch (Exception e) {
            System.err.println("❌ Email send failed: " + e.getMessage());
        }
    }
}