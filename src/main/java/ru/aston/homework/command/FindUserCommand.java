package ru.aston.homework.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FindUserCommand implements ActionCommand {

    private static final Logger log =
            LoggerFactory.getLogger(FindUserCommand.class);

    @Override
    public void execute() {
        log.info("Вызвана команда поиска пользователя");

        System.out.println("Поиск пользователя...");

        log.debug("Функциональность поиска пользователя ещё не реализована");
    }
}
