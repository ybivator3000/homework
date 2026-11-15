package ru.aston.notification.controller;

import jakarta.validation.Valid;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ru.aston.notification.dto.NotificationResponse;
import ru.aston.notification.dto.UserEvent;
import ru.aston.notification.dto.NotificationApiResponse;
import ru.aston.notification.service.EmailService;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;


@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final EmailService emailService;

    public NotificationController(EmailService emailService) {
        this.emailService = emailService;
    }

    @GetMapping
    public EntityModel<NotificationApiResponse> getApiInfo() {

        NotificationApiResponse response = new NotificationApiResponse(
                "Notification API",
                "API для отправки уведомлений по электронной почте"
        );

        return EntityModel.of(
                response,
                linkTo(NotificationController.class).withSelfRel(),
                linkTo(NotificationController.class)
                        .slash("send")
                        .withRel("send")
        );
    }

    @PostMapping("/send")
    public ResponseEntity<EntityModel<NotificationResponse>> sendEmail(
            @Valid @RequestBody UserEvent request
    ) {

        String subject = switch (request.operation()) {
            case CREATE -> "Аккаунт создан";
            case DELETE -> "Аккаунт удалён";
        };

        String text = switch (request.operation()) {
            case CREATE -> "Здравствуйте! Ваш аккаунт на сайте ваш сайт был успешно создан.";
            case DELETE -> "Здравствуйте! Ваш аккаунт был удалён.";
        };

        emailService.sendEmail(request.email(), subject, text);

        NotificationResponse response = new NotificationResponse(
                request.email(),
                request.operation(),
                "Уведомление успешно отправлено"
        );

        EntityModel<NotificationResponse> resource = EntityModel.of(
                response,
                linkTo(NotificationController.class).withRel("api"),
                linkTo(NotificationController.class)
                        .slash("send")
                        .withSelfRel()
        );

        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(resource);
    }
}