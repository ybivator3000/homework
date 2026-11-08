package ru.aston.notification.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.aston.notification.dto.UserEvent;
import ru.aston.notification.service.EmailService;
import tools.jackson.databind.json.JsonMapper;

@Component
public class UserEventConsumer {

    private static final Logger log =
            LoggerFactory.getLogger(UserEventConsumer.class);

    private final JsonMapper jsonMapper;
    private final EmailService emailService;

    public UserEventConsumer(JsonMapper jsonMapper, EmailService emailService) {
        this.jsonMapper = jsonMapper;
        this.emailService = emailService;
    }

    @KafkaListener(topics = "user-events")
    public void consume(String message) {
        UserEvent event = jsonMapper.readValue(message, UserEvent.class);

        if (event == null
                || event.email() == null
                || event.email().isBlank()
                || event.operation() == null) {
            throw new IllegalArgumentException("Invalid user event");
        }

        switch (event.operation()) {
            case CREATE -> emailService.sendEmail(
                    event.email(),
                    "Регистрация",
                    "Здравствуйте! Ваш аккаунт на сайте был успешно создан."
            );
            case DELETE -> emailService.sendEmail(
                    event.email(),
                    "Удаление аккаунта",
                    "Здравствуйте! Ваш аккаунт был удалён."
            );
        }

        log.info("Received user event: {}", event.operation());
    }
}
