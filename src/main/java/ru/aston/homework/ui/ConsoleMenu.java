package ru.aston.homework.ui;

public class ConsoleMenu {

    public void printMenu() {
        System.out.println("\n===== МЕНЮ УПРАВЛЕНИЯ ПОЛЬЗОВАТЕЛЯМИ =====");

        Command[] commands = Command.values();

        for (int i = 0; i < commands.length; i++) {
            System.out.printf("%d. %s%n",
                    i + 1,
                    commands[i].getDescription());
        }

        System.out.print("Выберите действие: ");
    }
}
