package ru.practicum.exception;

public class EndBeforeStartException extends RuntimeException {

    private static final String END_BEFORE_START_MESSAGE = "Дата конца, раньше даты начала";

    public EndBeforeStartException() {
        super(END_BEFORE_START_MESSAGE);
    }
}
