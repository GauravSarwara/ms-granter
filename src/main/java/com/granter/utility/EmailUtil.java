package com.granter.utility;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class EmailUtil {

    @Autowired
    private JavaMailSender mailSender;

    public void sendVerificationEmail(String toEmail, String token) {

        String subject = "Verify Your Account";
        String encodedToken = URLEncoder.encode(token, StandardCharsets.UTF_8);
        String verificationLink = "http://localhost:8080/api/auth/verify?token=" + encodedToken;

        String body = "Dear User,\n\n"
                + "Please click the below link to verify your account:\n"
                + verificationLink + "\n\n"
                + "If you did not register, please ignore this email.\n\n"
                + "Thanks & Regards,\nGranter Team";

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);
    }
}