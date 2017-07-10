package com.velox.sloan.workflows.notificator;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class NotificatorSpy extends Notificator {
    private String message = "";
    private List<String> addedMessages = new LinkedList<>();
    private Map<String, Integer> reqIdToNotifyCounter = new HashMap<>();

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

    @Override
    public void notifyAllMessages(String requestId) {
        reqIdToNotifyCounter.putIfAbsent(requestId, 0);
        reqIdToNotifyCounter.put(requestId, reqIdToNotifyCounter.get(requestId) + 1);

        super.notifyAllMessages(requestId);
    }

    public Map<String, Integer> getReqIdToNotifyCounter() {
        return reqIdToNotifyCounter;
    }
}
