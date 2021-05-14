package com.n26.transaction.exception.handler;

import com.n26.transaction.exception.InputValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class TransactionExceptionHandler {

    @ExceptionHandler(value= InputValidationException.class)
    public ResponseEntity<?> handleException(InputValidationException inputValidationException) {
        return ResponseEntity.status(Integer.parseInt(inputValidationException.getErrorCode())).build();
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleException() {
        return ResponseEntity.badRequest().build();
    }

}
