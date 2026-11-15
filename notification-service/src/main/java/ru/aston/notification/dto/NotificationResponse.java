package ru.aston.notification.dto;

public record NotificationResponse(  String email,
                                     UserEvent.Operation operation,
                                     String message
) {

}
