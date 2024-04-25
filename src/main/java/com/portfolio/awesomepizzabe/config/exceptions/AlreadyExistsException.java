package com.portfolio.awesomepizzabe.config.exceptions;

import com.portfolio.awesomepizzabe.config.exceptions.status.ConflictException;

public class AlreadyExistsException extends ConflictException {
    public AlreadyExistsException(String message) {
        super(message);
    }

    public AlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    public AlreadyExistsException(Throwable cause) {
        super(cause);
    }
}
