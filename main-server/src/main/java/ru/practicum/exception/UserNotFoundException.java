package ru.practicum.exception;

public class UserNotFoundException extends RuntimeException {

    private static String USER_NOT_FOUND_MESSAGE = "Пользователя с ID %s не найдено";

    public UserNotFoundException(Integer userId) {
        super(String.format(USER_NOT_FOUND_MESSAGE, userId));
    }
}
