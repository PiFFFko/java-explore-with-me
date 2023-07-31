package ru.practicum.exception;

import ru.practicum.model.enums.Status;

public class InvalidRequestStateException extends RuntimeException {


    private static final String INCORRECT_REQUEST_STATE_MESSAGE = "Запрос должно иметь статус PENDING. Текущее состояние - %s.";

    public InvalidRequestStateException(Status status) {
            super(String.format(INCORRECT_REQUEST_STATE_MESSAGE, status.toString()));
        }
}
