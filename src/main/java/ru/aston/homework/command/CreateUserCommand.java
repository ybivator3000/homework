
package ru.aston.homework.command;

import ru.aston.homework.ui.InputReader;

public class CreateUserCommand implements ActionCommand {

    private final InputReader inputReader;

    public CreateUserCommand(InputReader inputReader) {
        this.inputReader = inputReader;
    }

    @Override
    public void execute() {
        System.out.println("\n===== СОЗДАНИЕ ПОЛЬЗОВАТЕЛЯ =====");


        String name;

        while (true) {
            System.out.print("Введите имя: ");
            name = inputReader.readLine().trim();

            if (!name.isEmpty()) {
                break;
            }

            System.out.println("Имя не должно быть пустым. Попробуйте ещё раз.");
        }

        String email;

        while (true) {
            System.out.print("Введите почту: ");
            email = inputReader.readLine().trim();

            if (email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
                break;
            }

            System.out.println("Некорректный формат почты. Попробуйте ещё раз.");
        }

        int age;

        while (true) {
            System.out.print("Введите возраст: ");
            String input = inputReader.readLine().trim();

            try {
                age = Integer.parseInt(input);

                if (age < 0 || age > 150) {
                    System.out.println(
                            "Возраст должен быть от 0 до 150 лет. Попробуйте ещё раз."
                    );
                    continue;
                }

                break;
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
