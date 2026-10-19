package ru.aston.homework.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShowAllUsersCommand implements ActionCommand {

    private static final Logger log =
            LoggerFactory.getLogger(ShowAllUsersCommand.class);

    @Override
    public void execute() {
        log.info("Вызвана команда просмотра списка пользователей");

        System.out.println("Вывод списка пользователей...");

        log.debug("Функциональность просмотра списка пользователей ещё не реализована");
    }
}
