package ru.practicum.exception;

public class MaxRequestsReachedException extends RuntimeException {
    private static final String MAX_REQUESTS_REACHED_MESSAGE = "Доcтигнут лимит заявок на участие в количестве %s";

    public MaxRequestsReachedException(Integer participantLimit) {
        super(String.format(MAX_REQUESTS_REACHED_MESSAGE, participantLimit));
    }
}
