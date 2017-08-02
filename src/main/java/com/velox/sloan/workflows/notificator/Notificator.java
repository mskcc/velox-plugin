package com.velox.sloan.workflows.notificator;

public interface Notificator {
    void notifyMessage(String requestId, String message);

    String getMessageSeparator();
}
