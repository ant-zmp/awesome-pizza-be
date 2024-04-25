package com.portfolio.awesomepizzabe.config.exceptions.status;

import com.portfolio.awesomepizzabe.config.exceptions.BaseException;
import org.springframework.http.HttpStatus;

public class ConflictException extends BaseException {

    public ConflictException(String message) {
        super(message, HttpStatus.CONFLICT);
    }

    public ConflictException(String message, Throwable cause) {
        super(message, cause, HttpStatus.CONFLICT);
    }

    public ConflictException(Throwable cause) {
        super(cause, HttpStatus.CONFLICT);
    }
}
