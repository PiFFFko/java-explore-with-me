package ru.practicum.exception;

public class EventTooEarlyException extends RuntimeException {
    private static String EVENT_TOO_EARLY = "Событие не может быть раньше, чем через %s часа от текущего момента.";

    public EventTooEarlyException(Integer hoursBefore) {
        super(String.format(EVENT_TOO_EARLY, hoursBefore));
    }
}
