package ru.aston.homework.command;

import ru.aston.homework.ui.InputReader;
import ru.aston.homework.util.UserValidator;

public class CreateUserCommand implements ActionCommand {

    private final InputReader inputReader;
    private final UserValidator userValidator;

    public CreateUserCommand(InputReader inputReader) {
        this.inputReader = inputReader;
        this.userValidator = new UserValidator();
    }

    @Override
    public void execute() {
        System.out.println("\n===== СОЗДАНИЕ ПОЛЬЗОВАТЕЛЯ =====");

        String name;

        while (true) {
            System.out.print("Введите имя: ");
            name = inputReader.readLine().trim();

            if (userValidator.isValidName(name)) {
                break;
            }

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

                System.out.println(
                        "Возраст должен быть от 0 до 150 лет. Попробуйте ещё раз."
                );
            } catch (NumberFormatException e) {
                System.out.println(
                        "Введите целое число. Попробуйте ещё раз."
                );
            }
        }

        System.out.println("\nДанные пользователя:");
        System.out.println("Имя: " + name);
        System.out.println("Почта: " + email);
        System.out.println("Возраст: " + age);
    }
}
