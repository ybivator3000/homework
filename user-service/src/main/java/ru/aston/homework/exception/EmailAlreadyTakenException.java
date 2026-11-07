package ru.aston.homework.exception;

public class EmailAlreadyTakenException extends ServiceException {
    public EmailAlreadyTakenException(String email) {
        super("Электронная почта " + email + " уже занята");
    }
}
