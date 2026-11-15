package ru.aston.homework.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

public record UserUpdateRequest(
        @Schema(
                description = "ID обновляемого пользователя. Должен совпадать с ID в URL",
                example = "3"
        )
        @NotNull(message = "ID обязателен")
        @Positive(message = "ID должен быть положительным числом")
        Long id,

        @Schema(description = "Имя пользователя", example = "Анна")
        @NotBlank(message = "Имя не должно быть пустым")
        @Size(max = 100, message = "Имя не должно превышать 100 символов")
        String name,

        @Schema(description = "Электронная почта пользователя", example = "anna@example.com")
        @NotBlank(message = "Почта не должна быть пустой")
        @Size(max = 254, message = "Почта не должна превышать 254 символа")
        @Pattern(
                regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
                message = "Введите корректный адрес электронной почты"
        )
        String email,

        @Schema(description = "Возраст пользователя от 1 до 150 лет", example = "25")
        @NotNull(message = "Возраст обязателен")
        @Min(value = 1, message = "Возраст должен быть от 1 до 150 лет")
        @Max(value = 150, message = "Возраст должен быть от 1 до 150 лет")
        Integer age
) {
}