package com.auction.auth.services.impl;

import com.auction.auth.services.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailServiceImpl.class);

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${spring.mail.properties.mail.smtp.from:}")
    private String fromEmail;

    @Value("${spring.mail.username:}")
    private String smtpUsername;

    private String effectiveFrom() {
        if (fromEmail != null && !fromEmail.isBlank()) return fromEmail;
        if (smtpUsername != null && !smtpUsername.isBlank()) return smtpUsername;
        return "no-reply@example.com";
    }

    @Override
    public void sendWelcomeEmail(String toEmail, String name) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(effectiveFrom());
            message.setTo(toEmail);
            message.setSubject("Welcome to Our Platform");
            message.setText("Hello " + name + ",\n\nThanks for registering with us!\n\nRegards,\nAuthify Team");
            mailSender.send(message);
            log.info("Welcome email queued to {}", toEmail);
        } catch (Exception e) {
            log.warn("Failed to send welcome email to {}: {}", toEmail, e.getMessage(), e);
        }
    }

    @Override
    public void sendOtpEmail(String toEmail, String otp) throws MessagingException {
        System.out.println("Sending OTP " + otp + " to " + toEmail);
        Context context = new Context();
        context.setVariable("email", toEmail);
        context.setVariable("otp", otp);

        String process = templateEngine.process("verify-email", context);
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

        helper.setFrom(effectiveFrom());
        helper.setTo(toEmail);
        helper.setSubject("Account Verification OTP");
        helper.setText(process, true);

        mailSender.send(mimeMessage);
        log.info("Sent verification OTP to {}", toEmail);
    }

    @Override
    public void sendResetOtpEmail(String toEmail, String otp) throws MessagingException {
        Context context = new Context();
        context.setVariable("email", toEmail);
        context.setVariable("otp", otp);

        String process = templateEngine.process("password-reset-email", context);
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

        helper.setFrom(effectiveFrom());
        helper.setTo(toEmail);
        helper.setSubject("Password Reset");
        helper.setText(process, true);

        mailSender.send(mimeMessage);
        log.info("Sent reset OTP to {}", toEmail);
    }
}
