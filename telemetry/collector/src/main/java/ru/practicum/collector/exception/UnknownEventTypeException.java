package ru.practicum.collector.exception;

public class UnknownEventTypeException extends RuntimeException {
    public UnknownEventTypeException(String message) {
        super(message);
    }
}
