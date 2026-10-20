package ru.aston.homework.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.aston.homework.command.CommandManager;
import ru.aston.homework.command.CreateUserCommand;
import ru.aston.homework.command.ExitCommand;
import ru.aston.homework.command.ShowAllUsersCommand;
import ru.aston.homework.command.FindUserCommand;
import ru.aston.homework.command.UpdateUserCommand;
import ru.aston.homework.command.DeleteUserCommand;
import ru.aston.homework.service.UserService;

public class ConsoleRunner {

    private static final Logger log =
            LoggerFactory.getLogger(ConsoleRunner.class);

    private final ConsoleMenu menu;
    private final InputReader inputReader;
    private final CommandManager commandManager;

    public ConsoleRunner(
            ConsoleMenu menu,
            InputReader inputReader,
            CommandManager commandManager
    ) {
        this.menu = menu;
        this.inputReader = inputReader;
        this.commandManager = commandManager;
    }

    public void run() {
        log.info("Запуск консольного меню");

        boolean running = true;

        while (running) {
            menu.printMenu();

            String input = inputReader.readLine();
            int number;

            try {
                number = Integer.parseInt(input.trim());
            } catch (NumberFormatException e) {
                log.warn("Введён некорректный номер команды: {}", input);
                System.out.println("Введите корректный номер команды.");
                continue;
            }

            Command command = Command.fromNumber(number);

            if (command == null) {
                log.warn("Выбрана неизвестная команда: {}", number);
                System.out.println("Неизвестная команда.");
                continue;
            }

            commandManager.execute(command);

            if (command == Command.EXIT) {
                running = false;
            }
        }

        log.info("Завершение консольного меню");
    }

    public static CommandManager createCommandManager(
            InputReader inputReader,
            UserService userService
    ) {
        CommandManager manager = new CommandManager();

        manager.register(
                Command.CREATE,
                new CreateUserCommand(inputReader, userService)
        );

        manager.register(
                Command.SHOW_ALL,
                new ShowAllUsersCommand(userService)
        );


        manager.register(
                Command.FIND_BY_FIELD,
                new FindUserCommand(inputReader, userService)
        );


        manager.register(
                Command.UPDATE,
                new UpdateUserCommand(inputReader, userService)
        );


        manager.register(
                Command.DELETE,
                new DeleteUserCommand(inputReader, userService)
        );

        manager.register(Command.EXIT, new ExitCommand());

        log.debug("Зарегистрированы команды консольного меню");

        return manager;
    }
}
