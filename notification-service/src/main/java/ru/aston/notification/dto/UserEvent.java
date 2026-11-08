package ru.aston.notification.dto;

public record UserEvent(
        String email,
        Operation operation
) {
    public enum Operation {
        CREATE,
        DELETE
    }
}
