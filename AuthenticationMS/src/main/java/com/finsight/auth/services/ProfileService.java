package com.finsight.auth.services;

public interface ProfileService {
    void sendOtp(String email);
    void verifyOtp(String email, String otp);
    void sendResetOtp(String email);
    void resetPassword(String email, String otp, String newPassword);
}
