package ru.aston.notification.service;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(properties = {
        "app.mail.from=noreply@example.com",

        "spring.mail.host=localhost",
        "spring.mail.port=2525",
        "spring.mail.username=",
        "spring.mail.password=",
        "spring.mail.properties.mail.smtp.auth=false",
        "spring.mail.properties.mail.smtp.starttls.enable=false",

        "spring.kafka.listener.auto-startup=false",

        "resilience4j.circuitbreaker.instances.smtp.sliding-window-type=COUNT_BASED",
        "resilience4j.circuitbreaker.instances.smtp.sliding-window-size=2",
        "resilience4j.circuitbreaker.instances.smtp.minimum-number-of-calls=2",
        "resilience4j.circuitbreaker.instances.smtp.failure-rate-threshold=50",
        "resilience4j.circuitbreaker.instances.smtp.wait-duration-in-open-state=1s",
        "resilience4j.circuitbreaker.instances.smtp.permitted-number-of-calls-in-half-open-state=1",
        "resilience4j.circuitbreaker.instances.smtp.automatic-transition-from-open-to-half-open-enabled=true"
})
class EmailServiceCircuitBreakerTest {

    @Autowired
    private EmailService emailService;

    @Autowired
    private CircuitBreakerRegistry circuitBreakerRegistry;

    @MockitoBean
    private JavaMailSender mailSender;

    private CircuitBreaker circuitBreaker;

    @BeforeEach
    void setUp() {
        circuitBreaker = circuitBreakerRegistry.circuitBreaker("smtp");
        circuitBreaker.reset();
        reset(mailSender);
    }

    @Test
    void circuitBreaker_shouldOpenAndCloseAfterSmtpRecovery()
            throws InterruptedException {

        doThrow(new MailSendException("SMTP server unavailable"))
                .when(mailSender)
                .send(any(SimpleMailMessage.class));
        assertThatThrownBy(() ->
                emailService.sendEmail(
                        "user@example.com",
                        "Test",
                        "First attempt"
                )
        ).isInstanceOf(IllegalStateException.class);

        assertThatThrownBy(() ->
                emailService.sendEmail(
                        "user@example.com",
                        "Test",
                        "Second attempt"
                )
        ).isInstanceOf(IllegalStateException.class);

        assertThat(circuitBreaker.getState())
                .isEqualTo(CircuitBreaker.State.OPEN);

        assertThatThrownBy(() ->
                emailService.sendEmail(
                        "user@example.com",
                        "Test",
                        "Third attempt"
                )
        ).isInstanceOf(IllegalStateException.class);

        verify(mailSender, times(2))
                .send(any(SimpleMailMessage.class));

        Thread.sleep(1200);

        assertThat(circuitBreaker.getState())
                .isEqualTo(CircuitBreaker.State.HALF_OPEN);
        doNothing().when(mailSender)
                .send(any(SimpleMailMessage.class));

        emailService.sendEmail(
                "user@example.com",
                "Test",
                "Recovery attempt"
        );

        assertThat(circuitBreaker.getState())
                .isEqualTo(CircuitBreaker.State.CLOSED);

        verify(mailSender, times(3))
                .send(any(SimpleMailMessage.class));
    }
}