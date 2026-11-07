
package ru.aston.homework.command;

import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.aston.homework.dto.UserCreateRequest;
import ru.aston.homework.dto.UserResponse;
import ru.aston.homework.dto.UserUpdateRequest;
import ru.aston.homework.service.UserService;
import ru.aston.homework.ui.ConsoleFieldValidator;
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

class UpdateUserCommandTest {

    private final ByteArrayOutputStream output = new ByteArrayOutputStream();
    private PrintStream originalOut;
    private ValidatorFactory validatorFactory;

    @BeforeEach
    void setUp() {
        originalOut = System.out;
        System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));
        validatorFactory = Validation.buildDefaultValidatorFactory();
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        validatorFactory.close();
    }

    private UpdateUserCommand createCommand(String... inputs) {
        Queue<String> queue = new ArrayDeque<>(List.of(inputs));
        InputReader inputReader = queue::remove;

        UserService userService = new UserService() {

            @Override
            public UserResponse update(UserUpdateRequest request) {
                return new UserResponse(
                        request.id(),
                        request.name(),
                        request.email(),
                        request.age(),
                        LocalDateTime.now()
                );
            }

            @Override
            public UserResponse create(UserCreateRequest request) {
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

        ConsoleFieldValidator fieldValidator =
                new ConsoleFieldValidator(validatorFactory.getValidator());

        return new UpdateUserCommand(
                inputReader,
                userService,
                fieldValidator
        );
    }

    @Test
    void shouldAcceptValidUserData() {
        UpdateUserCommand command = createCommand(
                "1",
                "Иван",
                "ivan@example.com",
                "25"
        );

        command.execute();

        String result = output.toString(StandardCharsets.UTF_8);

        assertTrue(result.contains("Пользователь успешно обновлён"));
        assertTrue(result.contains("ID: 1"));
        assertTrue(result.contains("Иван"));
        assertTrue(result.contains("ivan@example.com"));
        assertTrue(result.contains("Возраст: 25"));
    }

    @Test
    void shouldAskForNameAgainWhenNameIsEmpty() {
        UpdateUserCommand command = createCommand(
                "1",
                "",
                "Иван",
                "ivan@example.com",
                "25"
        );

        command.execute();

        String result = output.toString(StandardCharsets.UTF_8);

        assertTrue(result.contains("Имя не должно быть пустым"));
        assertTrue(result.contains("Пользователь успешно обновлён"));
    }

    @Test
    void shouldAskForEmailAgainWhenEmailIsInvalid() {
        UpdateUserCommand command = createCommand(
                "1",
                "Иван",
                "wrong-email",
                "ivan@example.com",
                "25"
        );

        command.execute();

        String result = output.toString(StandardCharsets.UTF_8);

        assertTrue(result.contains("Введите корректный адрес электронной почты"));
        assertTrue(result.contains("Пользователь успешно обновлён"));
    }

    @Test
    void shouldAskForAgeAgainWhenAgeIsInvalid() {
        UpdateUserCommand command = createCommand(
                "1",
                "Иван",
                "ivan@example.com",
                "abc",
                "-5",
                "151",
                "25"
        );

        command.execute();

        String result = output.toString(StandardCharsets.UTF_8);

        assertTrue(result.contains("Возраст должен быть целым числом"));
        assertTrue(result.contains("Возраст должен быть от 1 до 150 лет"));
        assertTrue(result.contains("Возраст: 25"));
    }

    @Test
    void shouldRejectNonNumericId() {
        UpdateUserCommand command = createCommand("abc");

        command.execute();

        String result = output.toString(StandardCharsets.UTF_8);

        assertTrue(result.contains("Введите корректный числовой ID"));
    }

    @Test
    void shouldRejectNegativeId() {
        UpdateUserCommand command = createCommand("-1");

        command.execute();

        String result = output.toString(StandardCharsets.UTF_8);

        assertTrue(result.contains("ID должен быть положительным числом"));
    }
}
