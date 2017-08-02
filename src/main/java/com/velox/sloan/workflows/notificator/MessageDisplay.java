package com.velox.sloan.workflows.notificator;

public interface MessageDisplay {
    void displayWarningPopup(String message);

    void logInfoMessage(String msg);

    void logInfoMessage(String msg, Throwable t);

    void logErrorMessage(String message);

    void logErrorMessage(String message, Throwable t);

    void logDebugMessage(String message);

    void logDebugMessage(String message, Throwable t);
}
