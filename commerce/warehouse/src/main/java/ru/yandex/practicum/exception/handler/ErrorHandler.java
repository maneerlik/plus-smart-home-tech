package ru.yandex.practicum.exception.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.exception.ErrorResponse;
import ru.yandex.practicum.exception.NoSpecifiedProductInWarehouseException;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleNoSpecifiedProductInWarehouse(NoSpecifiedProductInWarehouseException e) {
        return new ErrorResponse(e.getMessage());
    }
}
