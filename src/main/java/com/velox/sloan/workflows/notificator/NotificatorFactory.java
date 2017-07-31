package com.velox.sloan.workflows.notificator;

import org.mskcc.util.BasicMail;

import java.util.Arrays;

public class NotificatorFactory {
    private PopupNotificator popupNotificator;
    private LogNotificator logNotificator;
    private EmailNotificator emailNotificator;

    public NotificatorFactory(MessageDisplay messageDisplay) {
        popupNotificator = new PopupNotificator(messageDisplay);
        logNotificator = new LogNotificator(messageDisplay);
        emailNotificator = new EmailNotificator(new BasicMail());
    }

    public BulkNotificator getPopupNotificator() {
        return new BulkNotificator(Arrays.asList(popupNotificator, logNotificator));
    }

    public BulkNotificator getLogNotificator() {
        return new BulkNotificator(Arrays.asList(logNotificator));
    }

    public BulkNotificator getEmailNotificator() {
        return new BulkNotificator(Arrays.asList(emailNotificator, logNotificator));
    }
}
