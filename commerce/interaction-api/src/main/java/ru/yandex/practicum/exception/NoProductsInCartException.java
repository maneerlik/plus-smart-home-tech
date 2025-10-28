package ru.yandex.practicum.exception;

public class NoProductsInCartException extends RuntimeException {
    public NoProductsInCartException(String message) {
        super(message);
    }
}
