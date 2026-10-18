package ru.aston.homework.util;

public class UserValidator {

    public boolean isValidName(String name) {
        return name != null && !name.trim().isEmpty();
    }

    public boolean isValidEmail(String email) {
        if (email == null) {
            return false;
        }

        return email.matches(
                "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
        );
    }

    public boolean isValidAge(int age) {
        return age >= 0 && age <= 150;
    }
}
