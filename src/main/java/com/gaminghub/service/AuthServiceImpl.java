package com.gaminghub.service;

import com.gaminghub.config.AppProperties;
import com.gaminghub.dto.AuthResponse;
import com.gaminghub.dto.UserResponse;
import com.gaminghub.entity.*;
import com.gaminghub.exception.ResourceNotFoundException;
import com.gaminghub.repository.OtpVerificationRepository;
import com.gaminghub.repository.RefreshTokenRepository;
import com.gaminghub.repository.UserRepository;
import com.gaminghub.security.JwtTokenService;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.UUID;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final OtpVerificationRepository otpVerificationRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenService jwtTokenService;
    private final PasswordEncoder passwordEncoder;
    private final AppProperties appProperties;
    private final SecureRandom secureRandom = new SecureRandom();

    public AuthServiceImpl(UserRepository userRepository,
                          OtpVerificationRepository otpVerificationRepository,
                          RefreshTokenRepository refreshTokenRepository,
                          JwtTokenService jwtTokenService,
                          PasswordEncoder passwordEncoder,
                          AppProperties appProperties) {
        this.userRepository = userRepository;
        this.otpVerificationRepository = otpVerificationRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtTokenService = jwtTokenService;
        this.passwordEncoder = passwordEncoder;
        this.appProperties = appProperties;
    }

    @Override
    @Transactional
    public AuthResponse loginWithGoogle(String idToken) {
        try {
            JsonNode payload = new RestTemplate().getForObject("https://oauth2.googleapis.com/tokeninfo?id_token=" + idToken, JsonNode.class);
            if (payload == null || payload.get("email") == null || payload.get("email_verified") == null || !payload.get("email_verified").asBoolean()) {
                throw new IllegalArgumentException("Google identity verification failed");
            }

            String email = payload.get("email").asText();
            String fullName = payload.has("name") ? payload.get("name").asText() : email;
            String profileImageUrl = payload.has("picture") ? payload.get("picture").asText() : null;

            User user = userRepository.findByEmail(email).orElseGet(() -> {
                User newUser = new User();
                newUser.setEmail(email);
                newUser.setFullName(fullName);
                newUser.setProfileImageUrl(profileImageUrl);
                newUser.setEmailVerified(true);
                newUser.setProvider("google");
                newUser.setProviderUserId(payload.has("sub") ? payload.get("sub").asText() : UUID.randomUUID().toString());
                newUser.setStatus(UserStatus.ACTIVE);
                newUser.setRole(Role.USER);
                return userRepository.save(newUser);
            });

            String accessToken = jwtTokenService.generateAccessToken(user.getEmail(), user.getId(), user.getRole().name());
            String refreshTokenValue = jwtTokenService.generateRefreshToken(user.getEmail(), user.getId());
            persistRefreshToken(user, refreshTokenValue);
            return new AuthResponse(accessToken, refreshTokenValue, mapUser(user));
        } catch (Exception e) {
            throw new IllegalArgumentException("Google authentication failed: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public AuthResponse requestOtpAndIssueToken(String email) {
        String normalizedEmail = email.trim().toLowerCase();
        User user = userRepository.findByEmail(normalizedEmail).orElseGet(() -> {
            User newUser = new User();
            newUser.setEmail(normalizedEmail);
            newUser.setFullName("User");
            newUser.setStatus(UserStatus.ACTIVE);
            newUser.setRole(Role.USER);
            return userRepository.save(newUser);
        });

        String otp = generateOtp();
        OtpVerification verification = otpVerificationRepository.findByEmail(normalizedEmail).orElse(new OtpVerification());
        verification.setEmail(normalizedEmail);
        verification.setOtpHash(hashValue(otp));
        verification.setExpiresAt(Instant.now().plusSeconds(appProperties.getSecurity().getOtp().getExpiryMinutes() * 60L));
        verification.setAttemptCount(0);
        otpVerificationRepository.save(verification);

        // Email sending is intentionally done via the notification layer in a real implementation.
        // The OTP is never returned in production responses.

        return new AuthResponse(null, null, mapUser(user));
    }

    @Override
    @Transactional
    public AuthResponse verifyOtp(String email, String otp) {
        String normalizedEmail = email.trim().toLowerCase();
        OtpVerification verification = otpVerificationRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new ResourceNotFoundException("OTP verification", normalizedEmail));

        if (verification.getExpiresAt().isBefore(Instant.now())) {
            throw new IllegalArgumentException("OTP has expired");
        }
        if (verification.getAttemptCount() >= appProperties.getSecurity().getOtp().getMaxAttempts()) {
            throw new IllegalArgumentException("Too many OTP verification attempts");
        }

        verification.setAttemptCount(verification.getAttemptCount() + 1);
        if (!hashValue(otp).equals(verification.getOtpHash())) {
            otpVerificationRepository.save(verification);
            throw new IllegalArgumentException("Invalid OTP");
        }

        User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", normalizedEmail));
        user.setEmailVerified(true);
        userRepository.save(user);

        verification.setVerifiedAt(Instant.now());
        otpVerificationRepository.save(verification);

        String accessToken = jwtTokenService.generateAccessToken(user.getEmail(), user.getId(), user.getRole().name());
        String refreshTokenValue = jwtTokenService.generateRefreshToken(user.getEmail(), user.getId());
        persistRefreshToken(user, refreshTokenValue);
        return new AuthResponse(accessToken, refreshTokenValue, mapUser(user));
    }

    @Override
    @Transactional
    public AuthResponse refreshToken(String refreshTokenValue) {
        String tokenHash = hashValue(refreshTokenValue);
        RefreshToken refreshToken = refreshTokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new ResourceNotFoundException("Refresh token", "provided token"));

        if (refreshToken.getRevokedAt() != null || refreshToken.getExpiresAt().isBefore(Instant.now())) {
            throw new IllegalArgumentException("Refresh token is expired or revoked");
        }

        User user = refreshToken.getUser();
        String newAccessToken = jwtTokenService.generateAccessToken(user.getEmail(), user.getId(), user.getRole().name());
        String newRefreshTokenValue = jwtTokenService.generateRefreshToken(user.getEmail(), user.getId());

        refreshToken.setRevokedAt(Instant.now());
        refreshTokenRepository.save(refreshToken);
        persistRefreshToken(user, newRefreshTokenValue);

        return new AuthResponse(newAccessToken, newRefreshTokenValue, mapUser(user));
    }

    @Override
    @Transactional
    public void logout(String refreshTokenValue, User principalUser) {
        if (refreshTokenValue == null || refreshTokenValue.isBlank()) {
            return;
        }
        String tokenHash = hashValue(refreshTokenValue);
        refreshTokenRepository.findByTokenHash(tokenHash)
                .ifPresent(token -> {
                    if (principalUser != null && !token.getUser().getId().equals(principalUser.getId())) {
                        throw new IllegalArgumentException("Refresh token does not belong to the current user");
                    }
                    token.setRevokedAt(Instant.now());
                    refreshTokenRepository.save(token);
                });
    }

    private void persistRefreshToken(User user, String refreshTokenValue) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setTokenHash(hashValue(refreshTokenValue));
        refreshToken.setExpiresAt(Instant.now().plusMillis(appProperties.getSecurity().getJwt().getRefreshTokenValidityMs()));
        refreshTokenRepository.save(refreshToken);
    }

    private String generateOtp() {
        int otp = 100000 + secureRandom.nextInt(900000);
        return String.valueOf(otp);
    }

    private String hashValue(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(value.trim().getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to hash value", e);
        }
    }

    private UserResponse mapUser(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getProfileImageUrl(),
                user.getCity(),
                user.getRole(),
                user.getStatus(),
                user.isEmailVerified(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}
