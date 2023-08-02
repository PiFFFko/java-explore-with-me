package ru.practicum.exception;

public class RequestAlreadyExistException extends RuntimeException {

    private static final String REQUEST_ALREADY_EXISTS_MESSAGE = "Вы уже учавствуте в событии с ID %s. Нельзя создать повторный запрос";

    public RequestAlreadyExistException(Integer eventId) {
        super(String.format(REQUEST_ALREADY_EXISTS_MESSAGE, eventId));
    }
}
