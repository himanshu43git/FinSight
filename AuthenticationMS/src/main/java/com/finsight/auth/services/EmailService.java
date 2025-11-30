package com.finsight.auth.services;

import jakarta.mail.MessagingException;

public interface EmailService {
    void sendWelcomeEmail(String toEmail, String name);
    void sendOtpEmail(String toEmail, String otp) throws MessagingException;
    void sendResetOtpEmail(String toEmail, String otp) throws MessagingException;
}
