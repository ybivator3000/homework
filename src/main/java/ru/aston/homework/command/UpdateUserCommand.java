package ru.aston.homework.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.aston.homework.dto.UserResponse;
import ru.aston.homework.dto.UserUpdateRequest;
import ru.aston.homework.exception.ServiceException;
import ru.aston.homework.service.UserService;
import ru.aston.homework.ui.InputReader;

public class UpdateUserCommand implements ActionCommand {

    private static final Logger log =
            LoggerFactory.getLogger(UpdateUserCommand.class);

    private final InputReader inputReader;
    private final UserService userService;

    public UpdateUserCommand(
            InputReader inputReader,
            UserService userService
    ) {
        this.inputReader = inputReader;
        this.userService = userService;
    }

    @Override
    public void execute() {
        log.info("Вызвана команда изменения пользователя");

        try {
            System.out.print("Введите ID пользователя для изменения: ");
            long id = Long.parseLong(inputReader.readLine().trim());

            if (id <= 0) {
                System.out.println("ID должен быть положительным числом.");
                return;
            }

            System.out.print("Введите новое имя: ");
            String name = inputReader.readLine().trim();

            System.out.print("Введите новую почту: ");
            String email = inputReader.readLine().trim();

            System.out.print("Введите новый возраст: ");
            int age = Integer.parseInt(inputReader.readLine().trim());

            UserUpdateRequest request =
                    new UserUpdateRequest(id, name, email, age);

            UserResponse updatedUser = userService.update(request);

            System.out.println("\nПользователь успешно обновлён!");
            System.out.println("ID: " + updatedUser.id());
            System.out.println("Имя: " + updatedUser.name());
            System.out.println("Почта: " + updatedUser.email());
            System.out.println("Возраст: " + updatedUser.age());

            log.info("Пользователь обновлён: id={}", id);

        } catch (NumberFormatException e) {
            System.out.println("ID и возраст должны быть целыми числами.");
        } catch (ServiceException e) {
            log.warn("Не удалось обновить пользователя: {}", e.getMessage());
            System.out.println("Не удалось обновить пользователя: " + e.getMessage());
        } catch (RuntimeException e) {
            log.error("Ошибка при обновлении пользователя", e);
            System.out.println("Произошла ошибка при обновлении пользователя.");
        }
    }
}
