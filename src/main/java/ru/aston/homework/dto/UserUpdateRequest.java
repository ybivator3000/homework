package ru.aston.homework.dto;

import java.util.Objects;

public record UserUpdateRequest(
        Long id,
        String name,
        String email,
        Integer age
) {
    public UserUpdateRequest {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(name, "name must not be null");
        Objects.requireNonNull(email, "email must not be null");
        Objects.requireNonNull(age, "age must not be null");
    }
}