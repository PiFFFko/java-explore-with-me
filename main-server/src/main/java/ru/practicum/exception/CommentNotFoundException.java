package ru.practicum.exception;

public class CommentNotFoundException extends RuntimeException {


    private static final String COMMENT_NOT_FOUND_MESSAGE = "Комментарий с ID %s не найден.";

    public CommentNotFoundException(Integer commentId) {
        super(String.format(COMMENT_NOT_FOUND_MESSAGE, commentId));
    }
}
