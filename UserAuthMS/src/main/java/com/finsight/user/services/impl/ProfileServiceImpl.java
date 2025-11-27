package com.auction.auth.services.impl;

import com.auction.auth.model.User;
import com.auction.auth.repository.UserRepository;
import com.auction.auth.services.EmailService;
import com.auction.auth.services.ProfileService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private static final Logger log = LoggerFactory.getLogger(ProfileServiceImpl.class);

    private static final long VERIFY_OTP_EXPIRY_MS = 10 * 60 * 1000L;
    private static final long RESET_OTP_EXPIRY_MS  = 15 * 60 * 1000L;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    private String generateOtp() {
        return String.valueOf(ThreadLocalRandom.current().nextInt(100000, 1000000));
    }

    @Override
    public void sendOtp(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        if (Boolean.TRUE.equals(user.getIsAccountVerified())) {
            log.debug("User {} already verified — skipping sendOtp", email);
            return;
        }

        String otp = generateOtp();
        long expireAt = Instant.now().toEpochMilli() + VERIFY_OTP_EXPIRY_MS;

        user.setVerifyOtp(otp);
        user.setVerifyOtpExpireAt(expireAt);
        userRepository.save(user);

        try {
            emailService.sendOtpEmail(user.getEmail(), otp);
            log.info("Sent verification OTP to {}", email);
        } catch (MessagingException e) {
            log.error("Failed to send verification OTP to {}: {}", email, e.getMessage(), e);
            throw new RuntimeException("Unable to send verification email", e);
        }
    }

    @Override
    public void verifyOtp(String email, String otp) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        String storedOtp = user.getVerifyOtp();
        long expireAt = user.getVerifyOtpExpireAt() == null ? 0L : user.getVerifyOtpExpireAt();

        if (storedOtp == null || !storedOtp.equals(otp)) {
            throw new IllegalArgumentException("Invalid OTP");
        }

        if (expireAt < Instant.now().toEpochMilli()) {
            throw new IllegalArgumentException("OTP expired");
        }

        user.setIsAccountVerified(true);
        user.setVerifyOtp(null);
        user.setVerifyOtpExpireAt(0L);
        userRepository.save(user);
        log.info("Verified account for {}", email);
    }

    @Override
    public void sendResetOtp(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        String otp = generateOtp();
        long expiry = Instant.now().toEpochMilli() + RESET_OTP_EXPIRY_MS;

        user.setResetOtp(otp);
        user.setResetOtpExpireAt(expiry);
        userRepository.save(user);

        try {
            emailService.sendResetOtpEmail(user.getEmail(), otp);
            log.info("Sent reset OTP to {}", email);
        } catch (MessagingException e) {
            log.error("Failed to send reset OTP to {}: {}", email, e.getMessage(), e);
            throw new RuntimeException("Unable to send reset OTP email", e);
        }
    }

    @Override
    public void resetPassword(String email, String otp, String newPassword) {
        if (newPassword == null || newPassword.length() < 8) {
            throw new IllegalArgumentException("New password must be at least 8 characters long.");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        String storedOtp = user.getResetOtp();
        long expireAt = user.getResetOtpExpireAt() == null ? 0L : user.getResetOtpExpireAt();

        if (storedOtp == null || !storedOtp.equals(otp)) {
            throw new IllegalArgumentException("Invalid reset OTP");
        }

        if (expireAt < Instant.now().toEpochMilli()) {
            throw new IllegalArgumentException("Reset OTP expired");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetOtp(null);
        user.setResetOtpExpireAt(0L);
        userRepository.save(user);

        log.info("Password reset successful for {}", email);
    }
}
