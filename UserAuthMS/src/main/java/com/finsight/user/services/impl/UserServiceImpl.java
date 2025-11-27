package com.auction.auth.services.impl;

import com.auction.auth.io.UserRequest;
import com.auction.auth.io.UserResponse;
import com.auction.auth.model.User;
import com.auction.auth.repository.UserRepository;
import com.auction.auth.services.EmailService;
import com.auction.auth.services.UserService;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

import jakarta.mail.MessagingException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private static final long VERIFY_OTP_EXPIRY_MS = 24 * 60 * 60 * 1000L; // 24 hours
    private static final long RESET_OTP_EXPIRY_MS = 15 * 60 * 1000L; // 15 minutes

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public UserServiceImpl(UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    private String generateOtp() {
        return String.valueOf(ThreadLocalRandom.current().nextInt(100000, 1000000));
    }

    @Override
    public Optional<UserResponse> createUser(UserRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("UserRequest cannot be null");
        }

        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new IllegalArgumentException("Email is required");
        }

        // Check email uniqueness
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("User with email " + request.getEmail() + " already exists.");
        }

        if (request.getPassword() == null || request.getPassword().length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters long.");
        }

        if (request.getRole() == null || request.getRole().isEmpty()) {
            request.setRole("ROLE_USER");
        }

        User user = new User();
        user.setUsername(request.getEmail()); // default username to email
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());

        // If you want to require email verification, set to false here.
        // If you want user to be immediately able to login, set true.
        // Defaulting to FALSE so send-otp / verify flow is used:
        user.setIsAccountVerified(false);

        User savedUser = userRepository.save(user);

        // Welcome email - log failures but don't fail registration if mail fails
        try {
            emailService.sendWelcomeEmail(savedUser.getEmail(), savedUser.getFirstName());
        } catch (Exception e) {
            log.warn("Failed to send welcome email to {} : {}", savedUser.getEmail(), e.getMessage());
        }

        return Optional.of(UserResponse.from(savedUser));
    }

    /*
     * ----------------- standard methods (kept minimal or straightforward)
     * -----------------
     */

    @Override
    public Optional<UserResponse> findById(String id) {
        if (id == null)
            return Optional.empty();
        return userRepository.findById(id).map(UserResponse::from);
    }

    @Override
    public Optional<UserResponse> findByEmail(String email) {
        if (email == null)
            return Optional.empty();
        return userRepository.findByEmail(email).map(UserResponse::from);
    }

    @Override
    public UserResponse updateUser(String id, UserRequest request) {
        // Basic validation
        if (id == null || id.isBlank() || request == null) {
            throw new IllegalArgumentException("id and request are required");
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));

        // Update email (and username default) — check uniqueness if changed
        String newEmail = request.getEmail();
        if (newEmail != null && !newEmail.isBlank() && !newEmail.equals(user.getEmail())) {
            if (userRepository.existsByEmail(newEmail)) {
                throw new IllegalArgumentException("Email already in use: " + newEmail);
            }
            // preserve old email for comparison when deciding username align
            String oldEmail = user.getEmail();
            user.setEmail(newEmail);

            // keep username aligned with email if it was equal to the old email or blank
            if (user.getUsername() == null || user.getUsername().isBlank()
                    || user.getUsername().equalsIgnoreCase(oldEmail)) {
                user.setUsername(newEmail);
            }

            // mark unverified on email change (common security pattern)
            user.setIsAccountVerified(false);
            user.setVerifyOtp(null);
            user.setVerifyOtpExpireAt(0L);
        }

        // Update password (encode before saving)
        String newPassword = request.getPassword();
        if (newPassword != null && !newPassword.isBlank()) {
            if (newPassword.length() < 8) {
                throw new IllegalArgumentException("Password must be at least 8 characters long.");
            }
            user.setPassword(passwordEncoder.encode(newPassword));
        }

        // Update role if provided
        if (request.getRole() != null && !request.getRole().isBlank()) {
            user.setRole(request.getRole());
        }

        // Update firstName / lastName if provided
        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }

        // Try block: attempt to update optional fields if present on DTO
        // (forward-compatible).
        try {
            // avatarUrl (optional)
            try {
                var m = request.getClass().getMethod("getAvatarUrl");
                Object val = m.invoke(request);
                if (val instanceof String s && s != null) {
                    if (!s.isBlank())
                        user.setAvatarUrl(s);
                }
            } catch (NoSuchMethodException ignored) {
                // method not present — skip
            }

            // preferences (optional) - stored as JSON string in entity
            try {
                var m2 = request.getClass().getMethod("getPreferences");
                Object val2 = m2.invoke(request);
                if (val2 instanceof String prefs && prefs != null) {
                    if (!prefs.isBlank())
                        user.setPreferences(prefs);
                }
            } catch (NoSuchMethodException ignored) {
                // method not present — skip
            }
        } catch (Exception ex) {
            // Reflection should not break update — log and continue.
            // If you have logging available, replace the next line with a logger call.
            System.err.println("Warning: optional fields update failed: " + ex.getMessage());
        }

        User saved = userRepository.save(user);
        return UserResponse.from(saved);
    }

    @Override
    public void deleteUser(String id) {
        // not implemented

        if(id == null || id.isBlank()) {
            throw new IllegalArgumentException("id is required");
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));

        userRepository.delete(user);

    }

    @Override
    public List<UserResponse> listAllUsers() {
        return userRepository.findAll().stream().map(UserResponse::from).toList();
    }

    @Override
    public void recordSuccessfulLogin(String email) {
        
        /* noop */

    }

    @Override
    public void recordFailedLoginAttempt(String email) {
        /* noop */ }

    @Override
    public void unlockAccount(String id) {
        /* noop */ }

    @Override
    public void lockAccount(String id, Duration duration) {
        /* noop */ }

    @Override
    public int unlockExpiredAccounts() {
        return 0;
    }

    @Override
    public void changePassword(String id, String currentPassword, String newPassword) {
        /* noop */ }

    /* ----------------- OTP related methods (implemented) ----------------- */

    @Override
    public void sendResetOtp(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("email is required");
        }

        User existingUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        String otp = generateOtp();
        long expiryTime = Instant.now().toEpochMilli() + RESET_OTP_EXPIRY_MS;

        existingUser.setResetOtp(otp);
        existingUser.setResetOtpExpireAt(expiryTime);

        userRepository.save(existingUser);

        try {
            emailService.sendResetOtpEmail(existingUser.getEmail(), otp);
            log.info("Reset OTP sent to {}", existingUser.getEmail());
        } catch (MessagingException e) {
            log.error("Unable to send reset OTP to {} : {}", existingUser.getEmail(), e.getMessage(), e);
            throw new RuntimeException("Unable to send reset OTP email: " + e.getMessage(), e);
        }
    }

    @Override
    public void resetPassword(String email, String otp, String newPassword) {
        if (email == null || email.isBlank() || otp == null || otp.isBlank() || newPassword == null
                || newPassword.isBlank()) {
            throw new IllegalArgumentException("email, otp and newPassword are required");
        }

        if (newPassword.length() < 8) {
            throw new IllegalArgumentException("New password must be at least 8 characters long.");
        }

        User existingUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        String storedResetOtp = existingUser.getResetOtp();
        long resetExpireAt = existingUser.getResetOtpExpireAt() == null ? 0L : existingUser.getResetOtpExpireAt();

        if (storedResetOtp == null || !storedResetOtp.equals(otp)) {
            throw new IllegalArgumentException("Invalid reset OTP");
        }

        if (resetExpireAt < Instant.now().toEpochMilli()) {
            throw new IllegalArgumentException("Reset OTP expired");
        }

        existingUser.setPassword(passwordEncoder.encode(newPassword));
        existingUser.setResetOtp(null);
        existingUser.setResetOtpExpireAt(0L);

        userRepository.save(existingUser);
    }

    @Override
    public void sendOtp(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("email is required");
        }

        User existingUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        // if already verified, nothing to do
        if (Boolean.TRUE.equals(existingUser.getIsAccountVerified())) {
            log.info("Account for {} already verified; skipping sendOtp", email);
            return;
        }

        String otp = generateOtp();
        long expiryTime = Instant.now().toEpochMilli() + VERIFY_OTP_EXPIRY_MS;

        existingUser.setVerifyOtp(otp);
        existingUser.setVerifyOtpExpireAt(expiryTime);

        userRepository.save(existingUser);

        try {
            emailService.sendOtpEmail(existingUser.getEmail(), otp);
            log.info("Verification OTP sent to {}", existingUser.getEmail());
        } catch (MessagingException e) {
            log.error("Unable to send verification OTP to {} : {}", existingUser.getEmail(), e.getMessage(), e);
            throw new RuntimeException("Unable to send verification email: " + e.getMessage(), e);
        }
    }

    @Override
    public void verifyOtp(String email, String otp) {
        if (email == null || email.isBlank() || otp == null || otp.isBlank()) {
            throw new IllegalArgumentException("email and otp are required");
        }

        User existingUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        String storedOtp = existingUser.getVerifyOtp();
        long expireAt = existingUser.getVerifyOtpExpireAt() == null ? 0L : existingUser.getVerifyOtpExpireAt();

        if (storedOtp == null || !storedOtp.equals(otp)) {
            throw new IllegalArgumentException("Invalid OTP");
        }

        if (expireAt < Instant.now().toEpochMilli()) {
            throw new IllegalArgumentException("OTP expired");
        }

        existingUser.setIsAccountVerified(true);
        existingUser.setVerifyOtp(null);
        existingUser.setVerifyOtpExpireAt(0L);

        userRepository.save(existingUser);
    }
}
