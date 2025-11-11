package org.mobilehub.identity_service.service;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class MailService {
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:}")
    private String configuredEmail;

    @Value("${app.otp.from-email:no-reply@example.com}")
    private String fromEmail;

    @Value("${app.otp.expire-minutes:1}")
    private long expireMinutes;

    private static final SecureRandom RANDOM = new SecureRandom();

    // key = email, value = entry
    private final Map<String, OtpEntry> concurrentOtpEntries = new ConcurrentHashMap<>();

    public void generateAndSendOtp(String email) {
        String otp = generateOtp();
        long expireAt = Instant.now().plusSeconds(expireMinutes * 60).toEpochMilli();

        concurrentOtpEntries.put(email, new OtpEntry(otp, expireAt));

        final String subject = "Your OTP code";
        String body = "Your OTP is: " + otp + "\nIt will expire in " + expireMinutes + " minutes.";

        System.out.println(body);

        String htmlBody = buildOtpEmailBody(otp, "MobileHub", expireMinutes);

        sendEmail(email, subject, htmlBody, true);
    }

    public boolean verify(String email, String otp) {
        OtpEntry entry = concurrentOtpEntries.get(email);
        if (entry == null) return false;
        if (Instant.now().toEpochMilli() > entry.expireAt) {
            concurrentOtpEntries.remove(email);
            return false;
        }
        boolean ok = entry.otp.equals(otp);
        if (ok) concurrentOtpEntries.remove(email);
        return ok;
    }

    private String generateOtp() {
        int num = RANDOM.nextInt(1_000_000);
        return String.format("%06d", num);
    }

    // scheduled cleanup every minute to remove expired entries
    @Scheduled(fixedRate = 60_000)
    public void cleanup() {
        long now = Instant.now().toEpochMilli();
        boolean isRemoved =
                concurrentOtpEntries.entrySet().removeIf(e -> e.getValue().expireAt < now);
        if(isRemoved) {
            System.out.println("OTP has been removed");
        }
    }

    @Async
    public void sendEmail(String to, String subject, String content, boolean isHtml) {
        if (configuredEmail == null || configuredEmail.isBlank()) {
            System.out.println("=== OTP Mail (console-mode) ===");
            System.out.println("To: " + to);
            System.out.println("Subject: " + subject);
            System.out.println(content);
            System.out.println("=== End OTP Mail ===");
            return;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, isHtml);

            mailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("=== SEND MAIL FAILED === " + e.getMessage());
        }
    }

    public String buildOtpEmailBody(String otp, String appName, long expireMinutes) {
        return """
<div style="font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 24px;">
    <div style="max-width: 480px; margin: auto; background-color: #ffffff; border-radius: 8px; padding: 24px; box-shadow: 0 2px 8px rgba(0,0,0,0.08); text-align: center;">
        <h2 style="color: #333; margin-bottom: 16px;">🔒 Verification Code</h2>
        <p style="color: #555; font-size: 14px; margin-bottom: 8px;">
            Use the following OTP to complete your verification with <b>%s</b>:
        </p>
        <div style="font-size: 32px; font-weight: bold; color: #1E90FF; letter-spacing: 4px; margin: 16px 0;">
            %s
        </div>
        <p style="color: #777; font-size: 12px;">
            This code will expire in <b>%d minute%s</b>. If you didn’t request this, you can safely ignore this email.
        </p>
    </div>
    <p style="text-align:center; font-size: 12px; color: #aaa; margin-top: 16px;">
        © %d %s. All rights reserved.
    </p>
</div>
""".formatted(appName, otp, expireMinutes, expireMinutes > 1 ? "s" : "", java.time.Year.now().getValue(), appName);
    }


    private record OtpEntry(String otp, long expireAt) {
    }
}
