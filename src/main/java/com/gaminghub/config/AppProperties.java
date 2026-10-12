package com.gaminghub.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    private final Security security = new Security();
    private final Email email = new Email();
    private String frontendOrigin;

    public static class Security {
        private final Jwt jwt = new Jwt();
        private final Otp otp = new Otp();
        private final Booking booking = new Booking();

        public static class Jwt {
            private String secret;
            private long accessTokenValidityMs = 900000L;
            private long refreshTokenValidityMs = 604800000L;

            public String getSecret() { return secret; }
            public void setSecret(String secret) { this.secret = secret; }
            public long getAccessTokenValidityMs() { return accessTokenValidityMs; }
            public void setAccessTokenValidityMs(long accessTokenValidityMs) { this.accessTokenValidityMs = accessTokenValidityMs; }
            public long getRefreshTokenValidityMs() { return refreshTokenValidityMs; }
            public void setRefreshTokenValidityMs(long refreshTokenValidityMs) { this.refreshTokenValidityMs = refreshTokenValidityMs; }
        }

        public static class Otp {
            private int expiryMinutes = 5;
            private int resendCooldownSeconds = 60;
            private int maxRequestsPerDay = 10;
            private int maxAttempts = 5;

            public int getExpiryMinutes() { return expiryMinutes; }
            public void setExpiryMinutes(int expiryMinutes) { this.expiryMinutes = expiryMinutes; }
            public int getResendCooldownSeconds() { return resendCooldownSeconds; }
            public void setResendCooldownSeconds(int resendCooldownSeconds) { this.resendCooldownSeconds = resendCooldownSeconds; }
            public int getMaxRequestsPerDay() { return maxRequestsPerDay; }
            public void setMaxRequestsPerDay(int maxRequestsPerDay) { this.maxRequestsPerDay = maxRequestsPerDay; }
            public int getMaxAttempts() { return maxAttempts; }
            public void setMaxAttempts(int maxAttempts) { this.maxAttempts = maxAttempts; }
        }

        public static class Booking {
            private int holdExpiryMinutes = 15;

            public int getHoldExpiryMinutes() { return holdExpiryMinutes; }
            public void setHoldExpiryMinutes(int holdExpiryMinutes) { this.holdExpiryMinutes = holdExpiryMinutes; }
        }

        public Jwt getJwt() { return jwt; }
        public Otp getOtp() { return otp; }
        public Booking getBooking() { return booking; }
    }

    public static class Email {
        private String from;

        public String getFrom() { return from; }
        public void setFrom(String from) { this.from = from; }
    }

    public Security getSecurity() { return security; }
    public Email getEmail() { return email; }
    public String getFrontendOrigin() { return frontendOrigin; }
    public void setFrontendOrigin(String frontendOrigin) { this.frontendOrigin = frontendOrigin; }
}
