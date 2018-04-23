package com.velox.sloan.workflows.notificator;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import com.velox.sloan.workflows.LoggerAndPopupDisplayer;
import com.velox.sloan.workflows.notificator.formatter.MessageFormatter;
import com.velox.sloan.workflows.notificator.formatter.RequestMessageFormatter;
import org.mskcc.util.notificator.Notificator;

import java.util.List;

public class BulkNotificator {
    private final List<Notificator> notificators;
    private final Multimap<String, String> messages = LinkedListMultimap.create();
    private final MessageFormatter requestMessageFormatter = new RequestMessageFormatter();

    public BulkNotificator(List<Notificator> notificators) {
        this.notificators = notificators;
    }

    public List<Notificator> getNotificators() {
        return notificators;
    }

    public void notifyAllMessages(String requestId) {
        if (!messages.get(requestId).isEmpty()) {
            for (Notificator notificator : notificators) {
                String message = requestMessageFormatter.getFormattedMessage(requestId, messages.get(requestId), notificator.getMessageSeparator());
                try {
                    notificator.notifyMessage(requestId, message);
                } catch (Exception e) {
                    LoggerAndPopupDisplayer.logError(e.getMessage(), e);
                }
            }
        }
    }

    public void addMessage(String requestId, String message) {
        messages.put(requestId, message);
    }

    public Multimap<String, String> getMessages() {
        return messages;
    }
}
