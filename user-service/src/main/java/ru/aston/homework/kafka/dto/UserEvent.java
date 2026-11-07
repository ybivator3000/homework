package ru.aston.homework.kafka.dto;

public record UserEvent(
        String email,
        Operation operation
) {

    public enum Operation {
        CREATE,
        DELETE
    }
}
