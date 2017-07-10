package com.velox.sloan.workflows.notificator;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import com.velox.sloan.workflows.notificator.formatter.MessageFormatter;
import com.velox.sloan.workflows.notificator.formatter.RequestMessageFormatter;

public abstract class Notificator {
    private final Multimap<String, String> messages = LinkedListMultimap.create();

    private final MessageFormatter requestMessageFormatter = new RequestMessageFormatter();
    public void notifyAllMessages(String requestId) {
        if (!messages.get(requestId).isEmpty()) {
            String message = requestMessageFormatter.getFormattedMessage(requestId, messages.get(requestId), getMessageSeparator());
            notifyMessage(requestId, message);
        }
    }

    public abstract void notifyMessage(String requestId, String message);

    public abstract String getMessageSeparator();

    public void addMessage(String requestId, String message) {
        messages.put(requestId, message);
    }

    public Multimap<String, String> getMessages() {
        return messages;
    }
}
