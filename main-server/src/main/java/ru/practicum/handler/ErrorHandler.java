package ru.practicum.handler;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.exception.*;

import java.time.LocalDateTime;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleFailValidation(MethodArgumentNotValidException e) {
        StringBuilder errorMessage = new StringBuilder();
        for (int i = 0; i < e.getBindingResult().getFieldErrorCount(); i++) {
            errorMessage.append(e.getBindingResult().getFieldErrors().get(i).getField() + " ");
            errorMessage.append(e.getBindingResult().getFieldErrors().get(i).getDefaultMessage() + ";");
        }
        return new ApiError(HttpStatus.BAD_REQUEST.toString(), e.getMessage(),
                errorMessage.toString(), LocalDateTime.now().toString());
    }

    @ExceptionHandler({BadRequestException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleBadRequest(RuntimeException e) {
        return new ApiError(HttpStatus.BAD_REQUEST.toString(), e.toString(), e.getMessage(), LocalDateTime.now().toString());
    }

    @ExceptionHandler({CategoryNotFoundException.class,
            UserNotFoundException.class,
            CompilationNotFoundException.class,
            EventNotFoundException.class,
            RequestNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFound(RuntimeException e) {
        return new ApiError(HttpStatus.NOT_FOUND.toString(), e.toString(), e.getMessage(), LocalDateTime.now().toString());
    }

    @ExceptionHandler({InvalidEventStateException.class,
            EventTooEarlyException.class,
            MaxRequestsReachedException.class,
            InvalidRequestStateException.class,
            RequestAlreadyExistException.class,
            SelfParticipationException.class,
            DataIntegrityViolationException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleConflict(RuntimeException e) {
        return new ApiError(HttpStatus.CONFLICT.toString(), e.toString(), e.getMessage(), LocalDateTime.now().toString());
    }

}
