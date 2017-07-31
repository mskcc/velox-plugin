package com.velox.sloan.workflows.notificator;

public class PopupNotificator implements Notificator {
    private MessageDisplay messageDisplay;

    public PopupNotificator(MessageDisplay messageDisplay) {
        this.messageDisplay = messageDisplay;
    }

    @Override
    public void notifyMessage(String requestId, String message) {
        messageDisplay.displayWarningPopup(message);
    }

    @Override
    public String getMessageSeparator() {
        return System.lineSeparator();
    }

}
