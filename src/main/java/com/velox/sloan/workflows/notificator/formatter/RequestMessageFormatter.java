package com.velox.sloan.workflows.notificator.formatter;

import org.apache.commons.lang3.StringUtils;

import java.util.Collection;

public class RequestMessageFormatter implements MessageFormatter {
    @Override
    public String getFormattedMessage(String requestId, Collection<String> messages, String separator) {
        return StringUtils.join(messages, separator);
    }
}
