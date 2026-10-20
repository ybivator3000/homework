package ru.aston.homework;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.aston.homework.command.CommandManager;
import ru.aston.homework.config.TransactionTemplate;
import ru.aston.homework.dao.user.UserDaoImpl;
import ru.aston.homework.ui.ConsoleInputReader;
import ru.aston.homework.ui.ConsoleMenu;
import ru.aston.homework.ui.ConsoleRunner;
import ru.aston.homework.util.UserValidator;
import ru.aston.homework.service.UserService;
import ru.aston.homework.service.impl.UserServiceImpl;

public class Main {

    private static final Logger log =
            LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        log.info("Starting user-service");

        ConsoleInputReader inputReader = new ConsoleInputReader();

        UserService userService = new UserServiceImpl(
                new UserDaoImpl(),
                new UserValidator(),
                new TransactionTemplate()
        );

        CommandManager commandManager =
                ConsoleRunner.createCommandManager(inputReader, userService);

        ConsoleMenu menu = new ConsoleMenu();

        ConsoleRunner runner = new ConsoleRunner(
                menu,
                inputReader,
                commandManager
        );

        runner.run();
    }
}
