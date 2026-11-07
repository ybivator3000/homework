
package ru.aston.homework.ui;

import jakarta.validation.Validator;
import ru.aston.homework.exception.ValidationException;

public class ConsoleFieldValidator {

    private final Validator validator;

    public ConsoleFieldValidator(Validator validator) {
        this.validator = validator;
    }

    public <T> void validate(
            Class<T> dtoClass,
            String fieldName,
            Object value
    ) {
        var violations = validator.validateValue(
                dtoClass, fieldName, value
        );

        if (!violations.isEmpty()) {
            throw new ValidationException(
                    violations.iterator().next().getMessage()
            );
        }
    }
}
