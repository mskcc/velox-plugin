package com.velox.sloan.workflows.notificator;

public interface MessageDisplay {
    void logInfoMessage(String msg);

    void displayWarningPopup(String message);

    void logErrorMessage(String message);

    void logDebugMessage(String message);
}
