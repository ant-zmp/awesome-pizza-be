package com.portfolio.awesomepizzabe.config.exceptions;

import org.springframework.http.HttpStatus;

public class AlreadyExistsException extends BaseException {

    public AlreadyExistsException(String message) {
        super(message, HttpStatus.CONFLICT);
    }

    public AlreadyExistsException(String message, Throwable cause) {
        super(message, cause, HttpStatus.CONFLICT);
    }

    public AlreadyExistsException(Throwable cause) {
        super(cause, HttpStatus.CONFLICT);
    }
}
