package com.portfolio.awesomepizzabe.config.exceptions;

import com.portfolio.awesomepizzabe.config.exceptions.status.ConflictException;

public class AssociatedEntityException extends ConflictException {
    public AssociatedEntityException(String message) {
        super(message);
    }

    public AssociatedEntityException(String message, Throwable cause) {
        super(message, cause);
    }

    public AssociatedEntityException(Throwable cause) {
        super(cause);
    }
}
