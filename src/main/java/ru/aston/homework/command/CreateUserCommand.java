package ru.aston.homework.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.aston.homework.ui.InputReader;
import ru.aston.homework.util.UserValidator;

public class CreateUserCommand implements ActionCommand {

    private static final Logger log =
            LoggerFactory.getLogger(CreateUserCommand.class);

    private final InputReader inputReader;
    private final UserValidator userValidator;

    public CreateUserCommand(InputReader inputReader) {
        this.inputReader = inputReader;
        this.userValidator = new UserValidator();
    }

    @Override
    public void execute() {
        log.info("Начато создание пользователя");

        System.out.println("\n===== СОЗДАНИЕ ПОЛЬЗОВАТЕЛЯ =====");

        String name;

        while (true) {
            System.out.print("Введите имя: ");
            name = inputReader.readLine().trim();

            if (userValidator.isValidName(name)) {
                break;
            }

            log.warn("Введено некорректное имя");

            System.out.println(
                    "Имя не должно быть пустым. Попробуйте ещё раз."
            );
        }

        String email;

        while (true) {
            System.out.print("Введите почту: ");
            email = inputReader.readLine().trim();

            if (userValidator.isValidEmail(email)) {
                break;
            }

            log.warn("Введён некорректный формат почты");

            System.out.println(
                    "Некорректный формат почты. Попробуйте ещё раз."
            );
        }

        int age;

        while (true) {
            System.out.print("Введите возраст: ");
            String input = inputReader.readLine().trim();

            try {
                age = Integer.parseInt(input);

                if (userValidator.isValidAge(age)) {
                    break;
                }

                log.warn("Введён возраст за пределами допустимого диапазона");

                System.out.println(
                        "Возраст должен быть от 0 до 150 лет. Попробуйте ещё раз."
                );
            } catch (NumberFormatException e) {
                log.warn("Вместо целого числа возраста введено некорректное значение");

                System.out.println(
                        "Введите целое число. Попробуйте ещё раз."
                );
            }
        }

        log.info("Данные пользователя успешно проверены");

        System.out.println("\nДанные пользователя:");
        System.out.println("Имя: " + name);
        System.out.println("Почта: " + email);
        System.out.println("Возраст: " + age);
    }
}
