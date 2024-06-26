package com.project.soshuceapi.services;

import com.project.soshuceapi.services.iservice.IEmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailService implements IEmailService {

    private final static String TAG = "EMAIL";

    @Autowired
    private JavaMailSender emailSender;
    @Value("${spring.mail.username}")
    private String from;

    @Override
    public void sendMail(String to, String subject, String body) throws MessagingException {
        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "utf-8");
            message.setContent(body, "text/html;charset=utf-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setFrom(from);
            emailSender.send(message);
        } catch (Exception e) {
            log.error(TAG + ": error.send.mail");
            log.error(TAG + ": " + e.getMessage());
            throw new MessagingException(e.getMessage());
        }
    }

}
