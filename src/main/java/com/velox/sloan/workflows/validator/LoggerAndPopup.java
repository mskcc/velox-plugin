package com.velox.sloan.workflows.validator;


import com.velox.sloan.workflows.notificator.MessageDisplay;

public class LoggerAndPopup {
    private static LoggerAndPopup INSTANCE;
    private static MessageDisplay messageDisplay;

    private LoggerAndPopup(MessageDisplay messageDisplay) {
        this.messageDisplay = messageDisplay;
    }

    public static LoggerAndPopup configure(MessageDisplay messageDisplay) {
        INSTANCE = new LoggerAndPopup(messageDisplay);
        return INSTANCE;
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

    public static LoggerAndPopup getInstance() {
        return INSTANCE;
    }
}
