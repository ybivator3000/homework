package ru.aston.homework.util;

import ru.aston.homework.dto.UserCreateRequest;
import ru.aston.homework.dto.UserUpdateRequest;
import ru.aston.homework.exception.ServiceException;

public class UserValidator {
    public UserValidator() {}
    public void validate(UserCreateRequest request){
        isValidAge(request.age());
        isValidName(request.name());
        isValidEmail(request.email());

    }
    public void validate(UserUpdateRequest request){
        isValidAge(request.age());
        isValidName(request.name());
        isValidEmail(request.email());
    }

    public void isValidName(String name) {
        if (name == null || name.isBlank()){
            throw new ServiceException("Имя не должно быть пустым");
        }

        if (name.length() > 100) {
            throw new ServiceException("Имя не должно превышать 100 символов");
        }
    }
    public void isValidEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new ServiceException("Почта не должна быть пустой");
        }

        if (!email.matches(
                "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
        )){
            throw new ServiceException("Некорректный формат почты");
        }

        if (email.length() > 254) {
            throw new ServiceException("Почта не должна превышать 254 символа");
        }
    }
    public void isValidAge(int age) {
        if (age <= 0 || age > 150){
            throw new ServiceException("Возраст должен быть от 1 до 150 лет");
        }
    }
}
