package ru.practicum;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.exception.EndBeforeStartException;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler({EndBeforeStartException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Exception handleBadRequest(RuntimeException e) {
        return e;
    }

}
