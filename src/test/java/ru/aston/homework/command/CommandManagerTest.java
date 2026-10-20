
package ru.aston.homework.command;

import org.junit.jupiter.api.Test;
import ru.aston.homework.ui.Command;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class CommandManagerTest {

    @Test
    void shouldExecuteRegisteredCommand() {
        CommandManager manager = new CommandManager();
        AtomicInteger calls = new AtomicInteger();

        manager.register(Command.CREATE, calls::incrementAndGet);
        manager.execute(Command.CREATE);

        assertEquals(1, calls.get());
    }

    @Test
    void shouldNotFailWhenCommandIsNotRegistered() {
        CommandManager manager = new CommandManager();

        assertDoesNotThrow(() -> manager.execute(Command.CREATE));
    }

    @Test
    void shouldRethrowExceptionFromCommand() {
        CommandManager manager = new CommandManager();

        manager.register(Command.CREATE, () -> {
            throw new IllegalStateException("Test error");
        });

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> manager.execute(Command.CREATE)
        );

        assertEquals("Test error", exception.getMessage());
    }
}
