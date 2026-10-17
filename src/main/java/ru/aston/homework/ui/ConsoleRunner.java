package ru.aston.homework.ui;

import ru.aston.homework.command.CommandManager;
import ru.aston.homework.command.CreateUserCommand;
import ru.aston.homework.command.ExitCommand;
import ru.aston.homework.command.ShowAllUsersCommand;
import ru.aston.homework.command.FindUserCommand;
import ru.aston.homework.command.UpdateUserCommand;
import ru.aston.homework.command.DeleteUserCommand;

public class ConsoleRunner {

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
        boolean running = true;

        while (running) {
            menu.printMenu();

            String input = inputReader.readLine();
            int number;

            try {
                number = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Введите корректный номер команды.");
                continue;
            }

            Command command = Command.fromNumber(number);

            if (command == null) {
                System.out.println("Неизвестная команда.");
                continue;
            }

            if (command == Command.EXIT) {
                commandManager.execute(command);
                running = false;
                continue;
            }

            commandManager.execute(command);
        }
    }

    public static CommandManager createCommandManager(InputReader inputReader) {
        CommandManager manager = new CommandManager();

        manager.register(Command.CREATE, new CreateUserCommand(inputReader));
        manager.register(Command.SHOW_ALL, new ShowAllUsersCommand());
        manager.register(Command.FIND_BY_FIELD, new FindUserCommand());
        manager.register(Command.UPDATE, new UpdateUserCommand());
        manager.register(Command.DELETE, new DeleteUserCommand());
        manager.register(Command.EXIT, new ExitCommand());

        return manager;
    }
}
