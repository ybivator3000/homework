package ru.aston.homework.command;

public class ExitCommand implements ActionCommand {

    @Override
    public void execute() {
        System.out.println("Завершение работы приложения...");
    }
}
