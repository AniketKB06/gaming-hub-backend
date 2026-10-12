package com.gaminghub.notification;

import com.gaminghub.config.AppProperties;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class SmtpEmailService implements EmailService {

    private final JavaMailSender mailSender;
    private final AppProperties appProperties;

    public SmtpEmailService(JavaMailSender mailSender, AppProperties appProperties) {
        this.mailSender = mailSender;
        this.appProperties = appProperties;
    }

    @Override
    public void sendOtpEmail(String to, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(appProperties.getEmail().getFrom());
        message.setTo(to);
        message.setSubject("Gaming Hub OTP Verification");
        message.setText("Your Gaming Hub verification code is: " + otp + "\nThis code expires in 5 minutes.");
        mailSender.send(message);
    }

    @Override
    public void sendTextEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(appProperties.getEmail().getFrom());
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }
}
