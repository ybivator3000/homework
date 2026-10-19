package ru.aston.homework.command;

public class UpdateUserCommand implements ActionCommand {

    @Override
    public void execute() {
        System.out.println("Изменение данных пользователя...");
    }
}
