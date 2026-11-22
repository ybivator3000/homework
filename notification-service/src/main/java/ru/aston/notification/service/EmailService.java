package ru.aston.notification.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger log =
            LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;
    private final CircuitBreaker circuitBreaker;
    private final String from;

    public EmailService(
            JavaMailSender mailSender,
            CircuitBreakerFactory<?, ?> circuitBreakerFactory,
            @Value("${app.mail.from}") String from
    ) {
        this.mailSender = mailSender;
        this.circuitBreaker = circuitBreakerFactory.create("smtp");
        this.from = from;
    }

    public void sendEmail(String to, String subject, String text) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(from);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);

        circuitBreaker.run(
                () -> {
                    mailSender.send(message);
                    log.info("Письмо отправлено на почту к {}", to);
                    return true;
                },
                exception -> {
                    log.error(
                            "Ошибка отправки письма. Circuit Breaker работает",
                            exception
                    );

                    throw new IllegalStateException(
                            "Невозможно отправить письмо на почту " + to,
                            exception
                    );
                }
        );
    }
}