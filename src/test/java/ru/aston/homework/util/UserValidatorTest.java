package ru.aston.homework.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserValidatorTest {

    private final UserValidator validator = new UserValidator();

    @Test
    void shouldAcceptValidName() {
        assertTrue(validator.isValidName("Иван"));
    }

    @Test
    void shouldRejectEmptyOrBlankName() {
        assertFalse(validator.isValidName(""));
        assertFalse(validator.isValidName("   "));
    }

    @Test
    void shouldRejectNullName() {
        assertFalse(validator.isValidName(null));
    }

    @Test
    void shouldAcceptValidEmail() {
        assertTrue(validator.isValidEmail("ivan@example.com"));
        assertTrue(validator.isValidEmail("ivan.petrov@mail.ru"));
    }

    @Test
    void shouldRejectInvalidEmail() {
        assertFalse(validator.isValidEmail("abc"));
        assertFalse(validator.isValidEmail("ivan@"));
        assertFalse(validator.isValidEmail("ivan@mail"));
    }

    @Test
    void shouldAcceptAgeWithinRange() {
        assertTrue(validator.isValidAge(0));
        assertTrue(validator.isValidAge(21));
        assertTrue(validator.isValidAge(150));
    }

    @Test
    void shouldRejectAgeOutsideRange() {
        assertFalse(validator.isValidAge(-1));
        assertFalse(validator.isValidAge(151));
    }
}
