package ru.practicum.exception;

public class RequestNotFoundException extends RuntimeException {

    private static final String REQUEST_NOT_FOUND_EXCEPTION = "Запрос с ID %s не найден.";


    public RequestNotFoundException(Integer requestId) {
        super(String.format(REQUEST_NOT_FOUND_EXCEPTION, requestId));
    }

}
