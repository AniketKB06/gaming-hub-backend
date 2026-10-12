package com.gaminghub.service;

import com.gaminghub.dto.AuthResponse;
import com.gaminghub.dto.UserResponse;
import com.gaminghub.entity.User;

public interface AuthService {
    AuthResponse loginWithGoogle(String idToken);
    AuthResponse requestOtpAndIssueToken(String email);
    AuthResponse verifyOtp(String email, String otp);
    AuthResponse refreshToken(String refreshTokenValue);
    void logout(String refreshTokenValue, User principalUser);
}
