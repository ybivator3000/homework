package ru.aston.homework.ui;

public enum Command {
    CREATE("Создать пользователя"),
    SHOW_ALL("Показать всех пользователей"),
    FIND_BY_FIELD("Найти пользователя"),
    UPDATE("Изменить пользователя"),
    DELETE("Удалить пользователя"),
    EXIT("Выход");

    private final String description;

    Command(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static Command fromNumber(int number) {
        Command[] commands = values();

        if (number < 1 || number > commands.length) {
            return null;
        }

        return commands[number - 1];
    }
}