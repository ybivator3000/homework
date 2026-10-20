package ru.aston.homework;

import ru.aston.homework.command.CommandManager;
import ru.aston.homework.ui.ConsoleInputReader;
import ru.aston.homework.ui.ConsoleMenu;
import ru.aston.homework.ui.ConsoleRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);
    public static void main(String[] args) {
        log.info("Starting user-service");

        System.out.println("Рабочая директория: "
                + System.getProperty("user.dir"));

        System.out.println("Конфигурация Logback: "
                + Main.class.getClassLoader()
                .getResource("logback.xml"));

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