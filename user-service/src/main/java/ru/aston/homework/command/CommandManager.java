package ru.aston.homework.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.aston.homework.ui.Command;

import java.util.EnumMap;
import java.util.Map;

public class CommandManager {

    private static final Logger log =
            LoggerFactory.getLogger(CommandManager.class);

    private final Map<Command, ActionCommand> commands =
            new EnumMap<>(Command.class);

    public void register(Command command, ActionCommand actionCommand) {
        commands.put(command, actionCommand);
        log.debug("Зарегистрирована команда: {}", command);
    }

    public void execute(Command command) {
        ActionCommand actionCommand = commands.get(command);

        if (actionCommand == null) {
            log.warn(
                    "Не удалось выполнить команду: {}. Команда не зарегистрирована",
                    command
            );
            System.out.println("Команда пока не реализована.");
            return;
        }

        log.info("Выполнение команды: {}", command);
        actionCommand.execute();
    }
}
