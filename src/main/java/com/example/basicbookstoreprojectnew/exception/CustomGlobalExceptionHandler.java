package com.example.basicbookstoreprojectnew.exception;

import com.example.basicbookstoreprojectnew.dto.CreateBookRequestDto;
import jakarta.persistence.EntityNotFoundException;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.management.RuntimeErrorException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class CustomGlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode statusCode,
            WebRequest request
    ) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST);

        List<String> fieldsOrderDto =
                Arrays.stream(CreateBookRequestDto.class.getDeclaredFields())
                        .map(Field::getName)
                        .toList();

        List<String> errors = ex.getBindingResult().getAllErrors().stream()
                .sorted(Comparator.comparingInt(error -> {
                    if (error instanceof FieldError fieldError) {
                        return fieldsOrderDto.indexOf(fieldError.getField());
                    }
                    return Integer.MAX_VALUE;
                }))

                .map(this::getErrorMessage)
                .toList();

        body.put("errors", errors);

        return new ResponseEntity<>(body, headers, statusCode);
    }

    private String getErrorMessage(ObjectError objectError) {

        return objectError.getDefaultMessage();
    }

    @ExceptionHandler(RuntimeErrorException.class)
    public ResponseEntity<Object> handleRuntimeException(RuntimeException runtimeException) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        body.put("error", runtimeException.getMessage());

        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Object> handleEntityNotFoundException(
            EntityNotFoundException entityNotFoundException) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.NOT_FOUND.value());
        body.put("error", entityNotFoundException.getMessage());

        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BookAlreadyExistsException.class)
    public ResponseEntity<Object> handleBookAlreadyExists(
            BookAlreadyExistsException bookAlreadyExistsException) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.CONFLICT.value());
        body.put("error", bookAlreadyExistsException.getMessage());

        return new ResponseEntity<>(body, HttpStatus.CONFLICT);
    }
}
