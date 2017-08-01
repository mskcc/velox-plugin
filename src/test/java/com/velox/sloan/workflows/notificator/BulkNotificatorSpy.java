package com.velox.sloan.workflows.notificator;

import java.util.*;

public class BulkNotificatorSpy extends BulkNotificator {
    private List<String> addedMessages = new LinkedList<>();
    private Map<String, Integer> reqIdToNotifyCounter = new HashMap<>();
    private MyNotificator mockNotificator;

    public BulkNotificatorSpy(MyNotificator mockNotificator) {
        super(Arrays.asList(mockNotificator));
        this.mockNotificator = mockNotificator;
    }

    public String getNotifiedMessage() {
        return mockNotificator.message;
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

    public static class MyNotificator implements Notificator {
        public String message = "";

        @Override
        public void notifyMessage(String requestId, String message) {
            this.message = message;
        }

        @Override
        public String getMessageSeparator() {
            return "";
        }
    }
}
