package com.lms.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendEmail(String to, String subject, String text) {
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setTo(to);
            msg.setSubject(subject);
            msg.setText(text);
            mailSender.send(msg);
            log.info("Email sent to {}", to);
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage());
        }
    }

    public void sendPasswordResetEmail(String to, String token) {
        String link = "http://localhost:3000/reset-password?token=" + token;
        sendEmail(to, "Password Reset Request",
                "Click the link to reset your password: " + link + "\n\nThis link expires in 30 minutes.");
    }

    public void sendOverdueNotice(String to, String memberName, String bookTitle, String dueDate) {
        sendEmail(to, "Overdue Book Notice",
                "Dear " + memberName + ",\n\nThe book \"" + bookTitle
                + "\" was due on " + dueDate + ". Please return it as soon as possible.\n\nThank you.");
    }

    public void sendReservationAvailableNotice(String to, String memberName, String bookTitle) {
        sendEmail(to, "Book Reservation Available",
                "Dear " + memberName + ",\n\nThe book \"" + bookTitle
                + "\" you reserved is now available for borrowing.\n\nThank you.");
    }
}
