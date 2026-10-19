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
            throw new ServiceException("Name cannot be null");
        }
    }
    public void isValidEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new ServiceException("Email cannot be null");
        }

        if (email.matches(
                "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
        )){
            throw new ServiceException("Email must be valid #test@mail.com");
        }
    }
    public void isValidAge(int age) {
        if (age <= 0 || age > 150){
            throw new ServiceException("Age must be between [0; 150]");
        }
    }
}
