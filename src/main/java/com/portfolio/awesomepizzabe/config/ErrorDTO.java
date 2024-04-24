package com.portfolio.awesomepizzabe.config;

public class ErrorDTO {

    private String message;
    private String exception;

    public ErrorDTO(Exception exception) {
        this.message = exception.getMessage();
        this.exception = exception.getClass().getSimpleName();
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
