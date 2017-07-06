package com.velox.sloan.workflows.notificator;

import java.util.LinkedList;
import java.util.List;

public class NotificatorSpy extends Notificator {
    private String message = "";
    private List<String> addedMessages = new LinkedList<>();

    @Override
    public void notifyMessage(String requestId, String message) {
        this.message = message;
    }

    @Override
    public String getMessageSeparator() {
        return "";
    }

    public String getNotifiedMessage() {
        return message;
    }

    public List<String> getAddedMessages() {
        return addedMessages;
    }

    @Override
    public void addMessage(String requestId, String message) {
        super.addMessage(requestId, message);
        addedMessages.add(message);
    }
}
