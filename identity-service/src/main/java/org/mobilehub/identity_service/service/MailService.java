package org.mobilehub.identity_service.service;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.mobilehub.identity_service.util.MailHtml;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;

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


    public OtpEntry generateAndSendOtp(String email) {
        OtpEntry otp = generateOtp();
        sendOtp(email, otp);
        return otp;
    }


    public void sendOtp(String email, OtpEntry otp) {
        final String subject = "Your OTP code";
        String body = "Your OTP is: " + otp + "\nIt will expire in " + expireMinutes + " minutes.";

        //System.out.println(body);

        String htmlBody = MailHtml.buildOtpHtmlBody(otp.otp(), expireMinutes);

        sendEmail(email, subject, htmlBody, true);
    }

    public OtpEntry generateOtp() {
        int num = RANDOM.nextInt(1_000_000);
        long expireAt = Instant.now().plusSeconds(expireMinutes * 60).toEpochMilli();

        return new OtpEntry(String.format("%06d", num), expireAt);
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
}
