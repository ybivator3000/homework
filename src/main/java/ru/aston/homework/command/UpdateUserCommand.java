package ru.aston.homework.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpdateUserCommand implements ActionCommand {

    private static final Logger log =
            LoggerFactory.getLogger(UpdateUserCommand.class);

    @Override
    public void execute() {
        log.info("Вызвана команда изменения пользователя");

        System.out.println("Изменение данных пользователя...");

        log.debug("Функциональность изменения пользователя ещё не реализована");
    }
}
