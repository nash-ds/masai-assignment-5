package com.hdfc.ledger.config;

import com.hdfc.ledger.dto.ErrorResponse;
import com.hdfc.ledger.exception.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponse validation(MethodArgumentNotValidException ex) {
        Map<String, String> fields = new LinkedHashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error ->
                fields.putIfAbsent(error.getField(), error.getDefaultMessage()));

        return new ErrorResponse(
                400,
                "Bad Request",
                "Validation failed",
                fields
        );
    }

    @ExceptionHandler({ PolicyNotFoundException.class, ClaimNotFoundException.class})
    public ErrorResponse notFound(DeskException ex) {
        return new ErrorResponse(
                404,
                "Not Found",
                ex.getMessage(),
                Map.of()
        );
    }

    @ExceptionHandler(DuplicatePolicyException.class)
    public ErrorResponse duplicate(DuplicatePolicyException ex) {
        return new ErrorResponse(
                409,
                "Conflict",
                ex.getMessage(),
                Map.of()
        );
    }

    @ExceptionHandler(InvalidRequestException.class)
    public ErrorResponse invalid(InvalidRequestException ex) {
        return new ErrorResponse(
                400,
                "Bad Request",
                ex.getMessage(),
                Map.of()
        );
    }
}
