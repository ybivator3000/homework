package ru.aston.homework.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.aston.homework.dto.UserResponse;
import ru.aston.homework.dto.UserUpdateRequest;
import ru.aston.homework.exception.ServiceException;
import ru.aston.homework.exception.ValidationException;
import ru.aston.homework.service.UserService;
import ru.aston.homework.ui.InputReader;
import ru.aston.homework.util.UserValidator;

public class UpdateUserCommand implements ActionCommand {

    private static final Logger log =
            LoggerFactory.getLogger(UpdateUserCommand.class);

    private final InputReader inputReader;
    private final UserService userService;
    private final UserValidator validator;

    public UpdateUserCommand(
            InputReader inputReader,
            UserService userService
    ) {
        this.inputReader = inputReader;
        this.userService = userService;
        this.validator = new UserValidator();
    }

    @Override
    public void execute() {
        log.info("Вызвана команда изменения пользователя");

        System.out.print("Введите ID пользователя для изменения: ");
        long id;

        try {
            id = Long.parseLong(inputReader.readLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Введите корректный числовой ID.");
            return;
        }

        if (id <= 0) {
            System.out.println("ID должен быть положительным числом.");
            return;
        }

        String name = readValidName();
        String email = readValidEmail();
        int age = readValidAge();

        UserUpdateRequest request =
                new UserUpdateRequest(id, name, email, age);

        try {
            UserResponse updatedUser = userService.update(request);

            System.out.println("\nПользователь успешно обновлён!");
            System.out.println("ID: " + updatedUser.id());
            System.out.println("Имя: " + updatedUser.name());
            System.out.println("Почта: " + updatedUser.email());
            System.out.println("Возраст: " + updatedUser.age());

            log.info("Пользователь обновлён: id={}", id);

        } catch (ValidationException e) {
            System.out.println("Ошибка валидации: " + e.getMessage());
            log.warn("Ошибка валидации при обновлении пользователя: {}",
                    e.getMessage());

        } catch (ServiceException e) {
            System.out.println(
                    "Не удалось обновить пользователя: " + e.getMessage()
            );
            log.warn("Не удалось обновить пользователя: {}", e.getMessage());
        }
    }

    private String readValidName() {
        while (true) {
            System.out.print("Введите новое имя: ");
            String name = inputReader.readLine().trim();

            try {
                validator.isValidName(name);
                return name;
            } catch (ValidationException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private String readValidEmail() {
        while (true) {
            System.out.print("Введите новую почту: ");
            String email = inputReader.readLine().trim();

            try {
                validator.isValidEmail(email);
                return email;
            } catch (ValidationException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private int readValidAge() {
        while (true) {
            System.out.print("Введите новый возраст: ");
            String input = inputReader.readLine().trim();

            try {
                int age = Integer.parseInt(input);
                validator.isValidAge(age);
                return age;
            } catch (NumberFormatException e) {
                System.out.println("Возраст должен быть целым числом.");
            } catch (ValidationException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
