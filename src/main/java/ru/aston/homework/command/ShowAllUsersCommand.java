package ru.aston.homework.command;

public class ShowAllUsersCommand implements ActionCommand {

    @Override
    public void execute() {
        System.out.println("Вывод списка пользователей...");
    }
}
