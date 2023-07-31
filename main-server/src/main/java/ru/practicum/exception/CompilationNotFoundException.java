package ru.practicum.exception;

public class CompilationNotFoundException extends RuntimeException {
    private static String COMPILATION_NOT_FOUND_MESSAGE = "Подборка с ID %s не найдено";

    public CompilationNotFoundException(Integer compilationId) {
        super(String.format(COMPILATION_NOT_FOUND_MESSAGE, compilationId));
    }
}
