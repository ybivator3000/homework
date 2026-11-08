package ru.aston.homework.exception;

public class UserNotFoundException extends ServiceException {
    public UserNotFoundException(Long id) {
        super("Пользователь с ID " + id + " не найден");
    }
}
