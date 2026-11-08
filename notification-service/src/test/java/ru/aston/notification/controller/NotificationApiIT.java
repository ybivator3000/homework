package ru.aston.notification.controller;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;
import jakarta.mail.Message;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class NotificationControllerIntegrationTest {

    private static final GreenMail greenMail;

    static {
        greenMail = new GreenMail(
                new ServerSetup(0, "127.0.0.1", ServerSetup.PROTOCOL_SMTP)
        );
        greenMail.start();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.mail.host", () -> "127.0.0.1");
        registry.add("spring.mail.port", () -> greenMail.getSmtp().getPort());
        registry.add("spring.mail.username", () -> "");
        registry.add("spring.mail.password", () -> "");

        registry.add("spring.mail.properties.mail.smtp.auth", () -> false);
        registry.add("spring.mail.properties.mail.smtp.starttls.enable", () -> false);

        registry.add("app.mail.from", () -> "noreply@example.com");

        registry.add("spring.kafka.listener.auto-startup", () -> false);
    }

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void clearEmails() throws Exception{
        greenMail.purgeEmailFromAllMailboxes();
    }

    @AfterAll
    static void stopGreenMail() {
        greenMail.stop();
    }

    @Test
    void sendEmail_whenCreateOperation_shouldSendRegistrationEmail()
            throws Exception {

        mockMvc.perform(post("/api/v1/notifications/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "user@example.com",
                                  "operation": "CREATE"
                                }
                                """))
                .andExpect(status().isAccepted());

        assertThat(greenMail.waitForIncomingEmail(5000, 1))
                .isTrue();

        MimeMessage[] messages = greenMail.getReceivedMessages();

        assertThat(messages).hasSize(1);

        MimeMessage message = messages[0];

        assertThat(message.getRecipients(Message.RecipientType.TO))
                .extracting(Object::toString)
                .contains("user@example.com");

        assertThat(message.getSubject())
                .isEqualTo("Аккаунт создан");

        assertThat(message.getContent().toString())
                .contains("Ваш аккаунт на сайте ваш сайт был успешно создан");
    }

    @Test
    void sendEmail_whenDeleteOperation_shouldSendDeletionEmail()
            throws Exception {

        mockMvc.perform(post("/api/v1/notifications/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "user@example.com",
                                  "operation": "DELETE"
                                }
                                """))
                .andExpect(status().isAccepted());

        assertThat(greenMail.waitForIncomingEmail(5000, 1))
                .isTrue();

        MimeMessage[] messages = greenMail.getReceivedMessages();

        assertThat(messages).hasSize(1);

        MimeMessage message = messages[0];

        assertThat(message.getRecipients(Message.RecipientType.TO))
                .extracting(Object::toString)
                .contains("user@example.com");

        assertThat(message.getSubject())
                .isEqualTo("Аккаунт удалён");

        assertThat(message.getContent().toString())
                .contains("Ваш аккаунт был удалён");
    }
}
