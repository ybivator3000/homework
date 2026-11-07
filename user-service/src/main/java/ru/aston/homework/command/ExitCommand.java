package ru.aston.homework.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExitCommand implements ActionCommand {

    private static final Logger log =
            LoggerFactory.getLogger(ExitCommand.class);

    @Override
    public void execute() {
        log.info("Завершение работы приложения");

        System.out.println("Завершение работы приложения...");
    }
}
