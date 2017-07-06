package com.velox.sloan.workflows.validator.converter;

public class SampleConvertionException extends RuntimeException {
    public SampleConvertionException(String message, Exception e) {
        super(message, e);
    }
}
