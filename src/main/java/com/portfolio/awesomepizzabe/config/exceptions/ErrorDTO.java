package com.portfolio.awesomepizzabe.config.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;

@Slf4j
public class ErrorDTO {

    private String message;
    private String exception;

    public ErrorDTO(Exception exception) {
        this.message = exception.getMessage();
        this.exception = exception.getClass().getSimpleName();
        if (exception instanceof BaseException || exception instanceof MethodArgumentNotValidException) {
            log.warn(this.message);
        } else {
            log.error(this.getMessage() + " : " + exception.getCause());
        }
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }

}
