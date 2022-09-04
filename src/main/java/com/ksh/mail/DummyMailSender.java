package com.ksh.mail;

import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

import javax.servlet.ServletOutputStream;

public class DummyMailSender implements MailSender {

    @Override
    public void send(SimpleMailMessage simpleMailMessage) throws MailException {
        System.out.println("From : " + simpleMailMessage.getTo()[0]);
        System.out.println("To : " + simpleMailMessage.getFrom());
        System.out.println("Subject : " + simpleMailMessage.getSubject());
        System.out.println("Text : " + simpleMailMessage.getText());
    }

    @Override
    public void send(SimpleMailMessage... simpleMailMessages) throws MailException {
    }
}
