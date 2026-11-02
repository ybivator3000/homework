package ru.aston.homework;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.context.ConfigurableApplicationContext;
import ru.aston.homework.command.CommandManager;
import ru.aston.homework.service.UserService;
import ru.aston.homework.ui.ConsoleInputReader;
import ru.aston.homework.ui.ConsoleMenu;
import ru.aston.homework.ui.ConsoleRunner;

public class Main {

    private static final Logger log =
            LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        log.info("Starting user-service");

        SpringApplication application = new SpringApplication(JpaProbeApplication.class);

        application.setWebApplicationType(WebApplicationType.NONE);

        try(ConfigurableApplicationContext context = application.run(args)) {
            ConsoleInputReader inputReader = new ConsoleInputReader();

            UserService userService = context.getBean(UserService.class);

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
            log.info("user-service stopped");
        }
    }
}
