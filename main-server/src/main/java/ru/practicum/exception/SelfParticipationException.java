package ru.practicum.exception;

public class SelfParticipationException extends RuntimeException {

    private static final String MAX_REQUESTS_REACHED_MESSAGE = "Вы не можете учавствовать в собственном событии. ID события - %s";

    public SelfParticipationException(Integer eventId) {
        super(String.format(MAX_REQUESTS_REACHED_MESSAGE, eventId));
    }
}
