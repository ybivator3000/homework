package ru.aston.homework.util;

import org.junit.jupiter.api.Test;
import ru.aston.homework.exception.ServiceException;

import static org.junit.jupiter.api.Assertions.*;

class UserValidatorTest {

    private final UserValidator validator = new UserValidator();

    @Test
    void shouldAcceptValidName() {
        assertDoesNotThrow(() -> validator.isValidName("Иван"));
    }

    @Test
    void shouldRejectEmptyOrBlankName() {
        assertThrows(ServiceException.class,
                () -> validator.isValidName(""));
        assertThrows(ServiceException.class,
                () -> validator.isValidName("   "));
    }

    @Test
    void shouldRejectNullName() {
        assertThrows(ServiceException.class,
                () -> validator.isValidName(null));
    }

    @Test
    void shouldAcceptValidEmail() {
        assertDoesNotThrow(() -> validator.isValidEmail("ivan@example.com"));
        assertDoesNotThrow(() -> validator.isValidEmail("ivan.petrov@mail.ru"));
    }

    @Test
    void shouldRejectInvalidEmail() {
        assertThrows(ServiceException.class,
                () -> validator.isValidEmail("abc"));
        assertThrows(ServiceException.class,
                () -> validator.isValidEmail("ivan@"));
        assertThrows(ServiceException.class,
                () -> validator.isValidEmail("ivan@mail"));
    }

    @Test
    void shouldAcceptAgeWithinRange() {
        assertDoesNotThrow(() -> validator.isValidAge(1));
        assertDoesNotThrow(() -> validator.isValidAge(21));
        assertDoesNotThrow(() -> validator.isValidAge(150));
    }

    @Test
    void shouldRejectAgeOutsideRange() {
        assertThrows(ServiceException.class,
                () -> validator.isValidAge(-1));
        assertThrows(ServiceException.class,
                () -> validator.isValidAge(0));
        assertThrows(ServiceException.class,
                () -> validator.isValidAge(151));
    }
}
