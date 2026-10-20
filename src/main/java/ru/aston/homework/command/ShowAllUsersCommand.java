package ru.aston.homework.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.aston.homework.dto.UserResponse;
import ru.aston.homework.service.UserService;

import java.util.List;

public class ShowAllUsersCommand implements ActionCommand {

    private static final Logger log =
            LoggerFactory.getLogger(ShowAllUsersCommand.class);

    private final UserService userService;

    public ShowAllUsersCommand(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void execute() {
        log.info("Вызвана команда просмотра списка пользователей");

        try {
            List<UserResponse> users = userService.findAll();

            if (users.isEmpty()) {
                System.out.println("Список пользователей пуст.");
                return;
            }

            System.out.println("\n===== СПИСОК ПОЛЬЗОВАТЕЛЕЙ =====");

            for (UserResponse user : users) {
                System.out.println("ID: " + user.id());
                System.out.println("Имя: " + user.name());
                System.out.println("Почта: " + user.email());
                System.out.println("Возраст: " + user.age());
                System.out.println("Создан: " + user.createdAt());
                System.out.println("------------------------------");
            }

            log.info("Выведено пользователей: {}", users.size());

        } catch (RuntimeException e) {
            log.error("Не удалось получить список пользователей", e);
            System.out.println("Не удалось получить список пользователей.");
        }
    }
}
