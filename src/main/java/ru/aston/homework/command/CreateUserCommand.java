package ru.aston.homework.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.aston.homework.dto.UserCreateRequest;
import ru.aston.homework.dto.UserResponse;
import ru.aston.homework.exception.ServiceException;
import ru.aston.homework.service.UserService;
import ru.aston.homework.ui.InputReader;

public class CreateUserCommand implements ActionCommand {

    private static final Logger log =
            LoggerFactory.getLogger(CreateUserCommand.class);

    private final InputReader inputReader;
    private final UserService userService;

    public CreateUserCommand(
            InputReader inputReader,
            UserService userService
    ) {
        this.inputReader = inputReader;
        this.userService = userService;
    }

    @Override
    public void execute() {
        log.info("Начато создание пользователя");

        System.out.println("\n===== СОЗДАНИЕ ПОЛЬЗОВАТЕЛЯ =====");

        String name;
        String email;
        int age;

        while (true) {
            System.out.print("Введите имя: ");
            name = inputReader.readLine().trim();

            if (!name.isBlank() && name.length() <= 100) {
                break;
            }

            System.out.println(
                    "Имя не должно быть пустым и не должно превышать 100 символов."
            );
        }

        while (true) {
            System.out.print("Введите почту: ");
            email = inputReader.readLine().trim();

            if (email.length() <= 254
                    && email.matches(
                    "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
            )) {
                break;
            }

            System.out.println("Введите корректный адрес электронной почты.");
        }

        while (true) {
            System.out.print("Введите возраст: ");
            String input = inputReader.readLine().trim();

            try {
                age = Integer.parseInt(input);

                if (age >= 1 && age <= 150) {
                    break;
                }

                System.out.println("Возраст должен быть от 1 до 150 лет.");
            } catch (NumberFormatException e) {
                System.out.println("Введите целое число.");
            }
        }

        try {
            UserCreateRequest request =
                    new UserCreateRequest(name, email, age);

            UserResponse createdUser = userService.create(request);

            System.out.println("\nПользователь успешно создан!");
            System.out.println("ID: " + createdUser.id());
            System.out.println("Имя: " + createdUser.name());
            System.out.println("Почта: " + createdUser.email());
            System.out.println("Возраст: " + createdUser.age());

            log.info(
                    "Пользователь создан: id={}",
                    createdUser.id()
            );

        } catch (ServiceException e) {
            log.warn("Не удалось создать пользователя: {}", e.getMessage());
            System.out.println("Не удалось создать пользователя: " + e.getMessage());
        }
    }
}
