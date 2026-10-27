package ru.aston.homework.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.aston.homework.dto.UserCreateRequest;
import ru.aston.homework.dto.UserResponse;
import ru.aston.homework.exception.ServiceException;
import ru.aston.homework.exception.ValidationException;
import ru.aston.homework.service.UserService;
import ru.aston.homework.ui.InputReader;
import ru.aston.homework.util.UserValidator;

public class CreateUserCommand implements ActionCommand {

    private static final Logger log =
            LoggerFactory.getLogger(CreateUserCommand.class);

    private final InputReader inputReader;
    private final UserService userService;
    private final UserValidator validator;

    public CreateUserCommand(
            InputReader inputReader,
            UserService userService
    ) {
        this.inputReader = inputReader;
        this.userService = userService;
        this.validator = new UserValidator();
    }

    @Override
    public void execute() {
        log.info("Начато создание пользователя");

        System.out.println("\n===== СОЗДАНИЕ ПОЛЬЗОВАТЕЛЯ =====");

        String name = readValidName();
        String email = readValidEmail();
        int age = readValidAge();

        UserCreateRequest request =
                new UserCreateRequest(name, email, age);

        try {
            UserResponse createdUser = userService.create(request);

            System.out.println("\nПользователь успешно создан!");
            System.out.println("ID: " + createdUser.id());
            System.out.println("Имя: " + createdUser.name());
            System.out.println("Почта: " + createdUser.email());
            System.out.println("Возраст: " + createdUser.age());

            log.info("Пользователь создан: id={}", createdUser.id());

        } catch (ValidationException e) {
            System.out.println("Ошибка валидации: " + e.getMessage());
            log.warn("Ошибка валидации при создании пользователя: {}",
                    e.getMessage());

        } catch (ServiceException e) {
            System.out.println(
                    "Не удалось создать пользователя: " + e.getMessage()
            );
            log.warn("Не удалось создать пользователя: {}", e.getMessage());
        }
    }

    private String readValidName() {
        while (true) {
            System.out.print("Введите имя: ");
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
            System.out.print("Введите почту: ");
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
            System.out.print("Введите возраст: ");
            String input = inputReader.readLine().trim();

            try {
                int age = Integer.parseInt(input);
                validator.isValidAge(age);
                return age;
            } catch (NumberFormatException e) {
                System.out.println("Введите целое число");
            } catch (ValidationException e) {
                System.out.println(e.getMessage());
            }
        }
    }

}
