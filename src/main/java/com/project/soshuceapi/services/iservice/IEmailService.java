package com.project.soshuceapi.services.iservice;

import jakarta.mail.MessagingException;

public interface IEmailService {

    void sendMail(String to, String subject, String body) throws MessagingException;

}
