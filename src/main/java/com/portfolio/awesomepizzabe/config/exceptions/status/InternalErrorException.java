package com.portfolio.awesomepizzabe.config.exceptions.status;

import com.portfolio.awesomepizzabe.config.exceptions.BaseException;
import org.springframework.http.HttpStatus;

public class InternalErrorException extends BaseException {

    public InternalErrorException(String message) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public InternalErrorException(String message, Throwable cause) {
        super(message, cause, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public InternalErrorException(Throwable cause) {
        super(cause, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
