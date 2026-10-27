package ru.aston.homework.util;

import ru.aston.homework.dto.UserCreateRequest;
import ru.aston.homework.dto.UserUpdateRequest;
import ru.aston.homework.exception.ValidationException;

public class UserValidator {

    public void validate(UserCreateRequest request) {
        isValidName(request.name());
        isValidEmail(request.email());
        isValidAge(request.age());
    }

    public void validate(UserUpdateRequest request) {
        isValidName(request.name());
        isValidEmail(request.email());
        isValidAge(request.age());
    }

    public void isValidName(String name) {
        if (name == null || name.isBlank()) {
            throw new ValidationException(
                    "Имя не должно быть пустым"
            );
        }

        if (name.length() > 100) {
            throw new ValidationException(
                    "Имя не должно превышать 100 символов"
            );
        }
    }

    public void isValidEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new ValidationException(
                    "Почта не должна быть пустой"
            );
        }

        if (email.length() > 254) {
            throw new ValidationException(
                    "Почта не должна превышать 254 символа"
            );
        }

        if (!email.matches(
                "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
        )) {
            throw new ValidationException(
                    "Введите корректный адрес электронной почты"
            );
        }
    }

    public void isValidAge(int age) {
        if (age < 1 || age > 150) {
            throw new ValidationException(
                    "Возраст должен быть от 1 до 150 лет"
            );
        }
    }
}
