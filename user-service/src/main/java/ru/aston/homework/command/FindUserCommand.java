package ru.aston.homework.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.aston.homework.dto.UserResponse;
import ru.aston.homework.service.UserService;
import ru.aston.homework.ui.InputReader;

import java.util.Optional;

public class FindUserCommand implements ActionCommand {

    private static final Logger log =
            LoggerFactory.getLogger(FindUserCommand.class);

    private final InputReader inputReader;
    private final UserService userService;

    public FindUserCommand(
            InputReader inputReader,
            UserService userService
    ) {
        this.inputReader = inputReader;
        this.userService = userService;
    }

    @Override
    public void execute() {
        log.info("Вызвана команда поиска пользователя");

        System.out.print("Введите ID пользователя: ");
        String input = inputReader.readLine().trim();

        long id;

        try {
            id = Long.parseLong(input);

            if (id <= 0) {
                System.out.println("ID должен быть положительным числом.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Введите корректный числовой ID.");
            return;
        }

        Optional<UserResponse> result = userService.findById(id);

        if (result.isEmpty()) {
            System.out.println("Пользователь с ID " + id + " не найден.");
            return;
        }

        UserResponse user = result.get();

        System.out.println("\n===== НАЙДЕННЫЙ ПОЛЬЗОВАТЕЛЬ =====");
        System.out.println("ID: " + user.id());
        System.out.println("Имя: " + user.name());
        System.out.println("Почта: " + user.email());
        System.out.println("Возраст: " + user.age());
        System.out.println("Создан: " + user.createdAt());

        log.info("Пользователь найден: id={}", id);
    }
}
