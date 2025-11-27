package com.auction.auth.services;

import com.auction.auth.io.UserRequest;
import com.auction.auth.io.UserResponse;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

public interface UserService {
    Optional<UserResponse> createUser(UserRequest request);

    Optional<UserResponse> findById(String id);

    Optional<UserResponse> findByEmail(String email);

    UserResponse updateUser(String id, UserRequest request);

    void deleteUser(String id);

    List<UserResponse> listAllUsers();

    void recordSuccessfulLogin(String email);

    void recordFailedLoginAttempt(String email);

    void unlockAccount(String id);

    void lockAccount(String id, Duration duration);

    int unlockExpiredAccounts();

    void changePassword(String id, String currentPassword, String newPassword);

    /* ---------- OTP related operations ---------- */

    /**
     * Generate and store a verification OTP for the given email and (optionally) send it via email.
     *
     * @param email target email
     */
    void sendOtp(String email);

    /**
     * Verify the OTP previously generated for the given email.
     *
     * @param email target email
     * @param otp   otp code
     */
    void verifyOtp(String email, String otp);

    /**
     * Generate and store a password-reset OTP for the given email and (optionally) send it via email.
     *
     * @param email target email
     */
    void sendResetOtp(String email);

    /**
     * Reset password using OTP. Verifies OTP validity and expiry, then updates the stored (encoded) password.
     *
     * @param email       target email
     * @param otp         otp provided by user
     * @param newPassword new plaintext password (must be validated/encoded)
     */
    void resetPassword(String email, String otp, String newPassword);
}
