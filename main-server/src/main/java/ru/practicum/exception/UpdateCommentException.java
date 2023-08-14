package ru.practicum.exception;

public class UpdateCommentException extends RuntimeException {

    private static final String UPDATE_COMMENT_MESSAGE = "Ошибка обновления комментария";

    public UpdateCommentException() {
        super(UPDATE_COMMENT_MESSAGE);
    }

    public UpdateCommentException(String message) {
        super(message);
    }

}
