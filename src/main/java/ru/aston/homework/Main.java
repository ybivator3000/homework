package ru.aston.homework;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.aston.homework.command.CommandManager;
import ru.aston.homework.config.HibernateUtil;
import ru.aston.homework.config.TransactionTemplate;
import ru.aston.homework.dao.user.UserDaoImpl;
import ru.aston.homework.service.UserService;
import ru.aston.homework.service.impl.UserServiceImpl;
import ru.aston.homework.ui.ConsoleInputReader;
import ru.aston.homework.ui.ConsoleMenu;
import ru.aston.homework.ui.ConsoleRunner;
import ru.aston.homework.util.UserValidator;

public class Main {

    private static final Logger log =
            LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        log.info("Starting user-service");

        try {
            ConsoleInputReader inputReader = new ConsoleInputReader();

            UserService userService = new UserServiceImpl(
                    new UserDaoImpl(),
                    new UserValidator(),
                    new TransactionTemplate()
            );

            CommandManager commandManager =
                    ConsoleRunner.createCommandManager(inputReader, userService);

            ConsoleMenu menu = new ConsoleMenu();

            ConsoleRunner runner = new ConsoleRunner(
                    menu,
                    inputReader,
                    commandManager
            );

            runner.run();
        } catch (ExceptionInInitializerError e) {
            log.error("Application initialization failed", e);
            System.err.println(
                    "Не удалось инициализировать приложение. Проверьте настройки БД и логи."
            );
        } catch (Exception e) {
            log.error("Application execution failed", e);
            System.err.println(
                    "Произошла ошибка при работе приложения. Подробности записаны в лог."
            );
        } finally {
            try {
                HibernateUtil.shutdown();
            } catch (Exception e) {
                log.error("Error while shutting down Hibernate", e);
            }

            log.info("user-service stopped");
        }
    }
}
