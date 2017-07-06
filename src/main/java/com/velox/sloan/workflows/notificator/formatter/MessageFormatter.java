package com.velox.sloan.workflows.notificator.formatter;

import java.util.Collection;

public interface MessageFormatter {
    String getFormattedMessage(String requestId, Collection<String> messages, String separator);
}
