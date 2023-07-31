package ru.practicum.exception;

public class EventNotFoundException extends RuntimeException {
    private static String EVENT_NOT_FOUND_MESSAGE = "Событие с ID %s не найдено";

    public EventNotFoundException(Integer eventId) {
        super(String.format(EVENT_NOT_FOUND_MESSAGE, eventId));
    }
}
