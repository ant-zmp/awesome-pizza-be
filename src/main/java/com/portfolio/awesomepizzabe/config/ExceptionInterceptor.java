package com.portfolio.awesomepizzabe.config;

import com.portfolio.awesomepizzabe.config.exceptions.BaseException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionInterceptor {

    @ExceptionHandler({BaseException.class})
    public ResponseEntity<ErrorDTO> handleBaseException(BaseException e) {
        return ResponseEntity.status(e.getHttpStatus().value())
                .body(new ErrorDTO(e));
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<ErrorDTO> handleException(Exception e) {
        return ResponseEntity.status(500)
                .body(new ErrorDTO(e));
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<ErrorDTO> handleValidationException(MethodArgumentNotValidException e) {
        return ResponseEntity.status(400)
                .body(new ErrorDTO(e));
    }
}
