package com.ss.newsportal;

import com.ss.newsportal.entity.Person;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class MailSenderHelper {
    private final JavaMailSender mailSender;

    public void send(String emailTo, String subject, String message) {

        SimpleMailMessage mailMessage = new SimpleMailMessage();

        mailMessage.setFrom("ssnewsportal@yandex.ru");
        mailMessage.setTo(emailTo);
        mailMessage.setSubject(subject);
        mailMessage.setText(message);

        mailSender.send(mailMessage);
    }

    public void sendEmailConfirmationCode(Person person, String baseUrl, String code) {
        String text = String.format("Здравствуйте, %s!\n" +
                "Добро пожаловать на наш новоствной портал.\n" +
                "Для активации аккаунта перейдите по ссылке: %s/activate/%s", person.getFirstName(), baseUrl, code);

        this.send(person.getEmail(), "Подтверждение аккаунта", text);
    }
}
