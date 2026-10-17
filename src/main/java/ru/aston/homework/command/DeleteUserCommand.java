package ru.aston.homework.command;

public class DeleteUserCommand implements ActionCommand {

    @Override
    public void execute() {
        System.out.println("Удаление пользователя...");
    }
}
