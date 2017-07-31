package com.velox.sloan.workflows.notificator;

public class LogNotificator implements Notificator {
    private MessageDisplay messageDisplay;

    public LogNotificator(MessageDisplay messageDisplay) {
        this.messageDisplay = messageDisplay;
    }

    @Override
    public void notifyMessage(String requestId, String message) {
        messageDisplay.logInfoMessage(message);
    }

    @Override
    public String getMessageSeparator() {
        return System.lineSeparator();
    }
}
