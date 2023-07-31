package ru.practicum.exception;

public class CategoryNotFoundException extends RuntimeException {
    private static String CATEGORY_NOT_FOUND_MESSAGE = "Категория с ID %s не найдено";

    public CategoryNotFoundException(Integer categoryId) {
        super(String.format(CATEGORY_NOT_FOUND_MESSAGE, categoryId));
    }
}
