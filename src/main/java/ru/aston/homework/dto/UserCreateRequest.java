package ru.aston.homework.dto;

import java.util.Objects;

public record UserCreateRequest(
        String name,
        String email,
        Integer age
) {
    //Валидотор это порешает наверн
    public UserCreateRequest {
        Objects.requireNonNull(name, "name must not be null");
        Objects.requireNonNull(email, "email must not be null");
        Objects.requireNonNull(age, "age must not be null");
    }
}