package com.velox.sloan.workflows.validator;

public class RequestValidationException extends RuntimeException {
    public RequestValidationException(String message, Exception e) {
        super(message, e);
    }
}
