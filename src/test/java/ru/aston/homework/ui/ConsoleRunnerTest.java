package ru.aston.homework.ui;

import org.junit.jupiter.api.Test;
import ru.aston.homework.command.ActionCommand;
import ru.aston.homework.command.CommandManager;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConsoleRunnerTest {

    @Test
    void shouldExecuteCreateAndExitCommands() {
        Queue<String> inputs = new ArrayDeque<>();
        inputs.add("1");
        inputs.add("6");

        InputReader inputReader = inputs::remove;

        AtomicInteger createCalls = new AtomicInteger();
        AtomicInteger exitCalls = new AtomicInteger();

        CommandManager commandManager = new CommandManager();

        ActionCommand createCommand = createCalls::incrementAndGet;
        ActionCommand exitCommand = exitCalls::incrementAndGet;

        commandManager.register(Command.CREATE, createCommand);
        commandManager.register(Command.EXIT, exitCommand);

        ConsoleRunner runner = new ConsoleRunner(
                new ConsoleMenu(),
                inputReader,
                commandManager
        );

        runner.run();

        assertEquals(1, createCalls.get());
        assertEquals(1, exitCalls.get());
    }

    @Test
    void shouldContinueWhenInputIsNotANumber() {
        Queue<String> inputs = new ArrayDeque<>();
        inputs.add("abc");
        inputs.add("6");

        InputReader inputReader = inputs::remove;

        AtomicInteger exitCalls = new AtomicInteger();

        CommandManager commandManager = new CommandManager();
        commandManager.register(
                Command.EXIT,
                exitCalls::incrementAndGet
        );

        ConsoleRunner runner = new ConsoleRunner(
                new ConsoleMenu(),
                inputReader,
                commandManager
        );

        runner.run();

        assertEquals(1, exitCalls.get());
    }

}
