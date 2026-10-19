package ru.aston.homework.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeleteUserCommand implements ActionCommand {

    private static final Logger log =
            LoggerFactory.getLogger(DeleteUserCommand.class);

    @Override
    public void execute() {
        log.info("Вызвана команда удаления пользователя");

        System.out.println("Удаление пользователя...");

        log.debug("Функциональность удаления пользователя ещё не реализована");
    }
}
