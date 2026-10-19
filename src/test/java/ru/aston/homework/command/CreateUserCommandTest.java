package ru.aston.homework.command;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.aston.homework.ui.InputReader;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.Queue;

import static org.junit.jupiter.api.Assertions.assertTrue;

class CreateUserCommandTest {

    private final ByteArrayOutputStream output = new ByteArrayOutputStream();
    private PrintStream originalOut;

    @BeforeEach
    void setUp() {
        originalOut = System.out;
        System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    private CreateUserCommand createCommand(String... inputs) {
        Queue<String> queue = new ArrayDeque<>(java.util.List.of(inputs));
        InputReader inputReader = queue::remove;
        return new CreateUserCommand(inputReader);
    }

    @Test
    void shouldAcceptValidUserData() {
        CreateUserCommand command = createCommand(
                "Иван",
                "ivan@example.com",
                "21"
        );

        command.execute();

        String result = output.toString(StandardCharsets.UTF_8);

        assertTrue(result.contains("Иван"));
        assertTrue(result.contains("ivan@example.com"));
        assertTrue(result.contains("21"));
    }

    @Test
    void shouldAskForNameAgainWhenNameIsEmpty() {
        CreateUserCommand command = createCommand(
                "",
                "Иван",
                "ivan@example.com",
                "21"
        );

        command.execute();

        String result = output.toString(StandardCharsets.UTF_8);

        assertTrue(result.contains("Name cannot be null"));
        assertTrue(result.contains("Иван"));
    }

    @Test
    void shouldAskForEmailAgainWhenEmailIsInvalid() {
        CreateUserCommand command = createCommand(
                "Иван",
                "abc",
                "ivan@example.com",
                "21"
        );

        command.execute();

        String result = output.toString(StandardCharsets.UTF_8);

        assertTrue(result.contains("Email must be valid"));
        assertTrue(result.contains("ivan@example.com"));
    }

    @Test
    void shouldAskForAgeAgainWhenAgeIsInvalid() {
        CreateUserCommand command = createCommand(
                "Иван",
                "ivan@example.com",
                "йцвыаы",
                "-5",
                "151",
                "21"
        );

        command.execute();

        String result = output.toString(StandardCharsets.UTF_8);

        assertTrue(result.contains("Введите целое число"));
        assertTrue(result.contains("Age must be between"));
        assertTrue(result.contains("Возраст: 21"));
    }
}
