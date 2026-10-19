package ru.aston.homework.command;

public class FindUserCommand implements ActionCommand {

    @Override
    public void execute() {
        System.out.println("Поиск пользователя...");
    }
}
