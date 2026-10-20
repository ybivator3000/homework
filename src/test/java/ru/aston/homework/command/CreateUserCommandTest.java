package ru.aston.homework.command;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.aston.homework.dto.UserCreateRequest;
import ru.aston.homework.dto.UserResponse;
import ru.aston.homework.dto.UserUpdateRequest;
import ru.aston.homework.service.UserService;
import ru.aston.homework.ui.InputReader;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Optional;
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
        Queue<String> queue = new ArrayDeque<>(List.of(inputs));
        InputReader inputReader = queue::remove;

        UserService userService = new UserService() {
            @Override
            public UserResponse create(UserCreateRequest request) {
                return new UserResponse(
                        1L,
                        request.name(),
                        request.email(),
                        request.age(),
                        LocalDateTime.now()
                );
            }

            @Override
            public UserResponse update(UserUpdateRequest request) {
                throw new UnsupportedOperationException();
            }

            @Override
            public Optional<UserResponse> findById(Long id) {
                throw new UnsupportedOperationException();
            }

            @Override
            public List<UserResponse> findAll() {
                throw new UnsupportedOperationException();
            }

            @Override
            public void delete(Long id) {
                throw new UnsupportedOperationException();
            }
        };

        return new CreateUserCommand(inputReader, userService);
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
        assertTrue(result.contains("Возраст: 21"));
        assertTrue(result.contains("Пользователь успешно создан"));
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

        assertTrue(result.contains("Имя не должно быть пустым"));
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

        assertTrue(result.contains("Введите корректный адрес электронной почты"));
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
        assertTrue(result.contains("Возраст должен быть от 1 до 150 лет"));
        assertTrue(result.contains("Возраст: 21"));
    }
}
