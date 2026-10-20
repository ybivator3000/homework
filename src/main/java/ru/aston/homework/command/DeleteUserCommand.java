package ru.aston.homework.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.aston.homework.exception.ServiceException;
import ru.aston.homework.service.UserService;
import ru.aston.homework.ui.InputReader;

public class DeleteUserCommand implements ActionCommand {

    private static final Logger log =
            LoggerFactory.getLogger(DeleteUserCommand.class);

    private final InputReader inputReader;
    private final UserService userService;

    public DeleteUserCommand(
            InputReader inputReader,
            UserService userService
    ) {
        this.inputReader = inputReader;
        this.userService = userService;
    }

    @Override
    public void execute() {
        log.info("Вызвана команда удаления пользователя");

        System.out.print("Введите ID пользователя для удаления: ");
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

        try {
            userService.delete(id);

            System.out.println("Пользователь успешно удалён.");
            log.info("Пользователь удалён: id={}", id);

        } catch (ServiceException e) {
            log.warn("Не удалось удалить пользователя с ID {}: {}", id, e.getMessage());
            System.out.println("Не удалось удалить пользователя: " + e.getMessage());

        } catch (RuntimeException e) {
            log.error("Ошибка при удалении пользователя с ID {}", id, e);
            System.out.println("Произошла ошибка при удалении пользователя.");
        }
    }
}
