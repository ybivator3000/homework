package ru.aston.homework;

import ru.aston.homework.command.CommandManager;
import ru.aston.homework.ui.ConsoleInputReader;
import ru.aston.homework.ui.ConsoleMenu;
import ru.aston.homework.ui.ConsoleRunner;

public class Main {
    public static void main(String[] args) {
        ConsoleInputReader inputReader = new ConsoleInputReader();
        CommandManager commandManager =
                ConsoleRunner.createCommandManager(inputReader);

        ConsoleMenu menu = new ConsoleMenu();

        ConsoleRunner runner = new ConsoleRunner(
                menu,
                inputReader,
                commandManager
        );

        runner.run();
    }
}
