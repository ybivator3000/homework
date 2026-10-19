package ru.aston.homework.command;

import ru.aston.homework.ui.Command;

import java.util.EnumMap;
import java.util.Map;

public class CommandManager {

    private final Map<Command, ActionCommand> commands =
            new EnumMap<>(Command.class);

    public void register(Command command, ActionCommand actionCommand) {
        commands.put(command, actionCommand);
    }

    public void execute(Command command) {
        ActionCommand actionCommand = commands.get(command);

        if (actionCommand == null) {
            System.out.println("Команда пока не реализована.");
            return;
        }

        actionCommand.execute();
    }
}
