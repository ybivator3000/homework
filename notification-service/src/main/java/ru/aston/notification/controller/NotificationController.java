package ru.aston.notification.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.aston.notification.dto.UserEvent;
import ru.aston.notification.service.EmailService;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final EmailService emailService;

    public NotificationController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/send")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void sendEmail(@Valid @RequestBody UserEvent request) {
        String subject = switch (request.operation()) {
            case CREATE -> "Аккаунт создан";
            case DELETE -> "Аккаунт удалён";
        };

        String text = switch (request.operation()) {
            case CREATE -> "Здравствуйте! Ваш аккаунт на сайте ваш сайт был успешно создан.";
            case DELETE -> "Здравствуйте! Ваш аккаунт был удалён.";
        };

        emailService.sendEmail(request.email(), subject, text);
    }
}