package com.gaminghub.notification;

public interface EmailService {
    void sendOtpEmail(String to, String otp);
    void sendTextEmail(String to, String subject, String body);
}
