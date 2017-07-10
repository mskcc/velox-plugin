package com.velox.sloan.workflows;


import com.velox.sloan.workflows.notificator.MessageDisplay;

public class LoggerAndPopupDisplayer {
    private static MessageDisplay messageDisplay = new NullMessageDisplay();

    private LoggerAndPopupDisplayer(MessageDisplay messageDisplay) {
        this.messageDisplay = messageDisplay;
    }

    public static LoggerAndPopupDisplayer configure(MessageDisplay messageDisplay) {
        return new LoggerAndPopupDisplayer(messageDisplay);
    }

    public static void logError(String message) {
        messageDisplay.logErrorMessage(message);
    }

    public static void logInfo(String message) {
        messageDisplay.logInfoMessage(message);
    }

    public static void logDebug(String message) {
        messageDisplay.logDebugMessage(message);
    }

    private static class NullMessageDisplay implements MessageDisplay {
        @Override
        public void logInfoMessage(String msg) {
        }

        @Override
        public void displayWarningPopup(String message) {
        }

        @Override
        public void logErrorMessage(String message) {
        }

        @Override
        public void logDebugMessage(String message) {
        }
    }
}
