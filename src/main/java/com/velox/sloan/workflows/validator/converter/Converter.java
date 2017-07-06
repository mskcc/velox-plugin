package com.velox.sloan.workflows.validator.converter;

public interface Converter<R, T> {
    T convert(R toConvert);
}
