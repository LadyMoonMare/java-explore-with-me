package ru.yandex.practicum.exception.handler;

import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.exception.ConflictException;
import ru.yandex.practicum.exception.NotFoundException;

import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleInvalidRequestData(final RuntimeException e) {
        log.warn("Error {}, message {}",HttpStatus.BAD_REQUEST, e.getMessage());
        return Map.of(
                "status","BAD_REQUEST",
                "reason","Incorrectly made request.",
                "message", e.getMessage(),
                "timestamp", LocalDateTime.now().toString()
        );
    }

    @ExceptionHandler(ConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleConflictData(final RuntimeException e) {
        log.warn("Error {}, message {}",HttpStatus.CONFLICT, e.getMessage());
        return Map.of(
                "status","CONFLICT",
                "reason","Integrity constraint has been violated.",
                "message", e.getMessage(),
                "timestamp", LocalDateTime.now().toString()
        );
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFoundData(final RuntimeException e) {
        log.warn("Error {}, message {}",HttpStatus.NOT_FOUND, e.getMessage());
        return Map.of(
                "status","NOT_FOUND",
                "reason","The required object was not found.",
                "message", e.getMessage(),
                "timestamp", LocalDateTime.now().toString()
        );
    }
}
