package com.velox.sloan.workflows.notificator;

import org.mskcc.util.BasicMail;

public class NotificatorFactory {
    private PopupNotificator popupNotificator;
    private LogNotificator logNotificator;
    private EmailNotificator emailNotificator;

    public NotificatorFactory(MessageDisplay messageDisplay) {
        popupNotificator = new PopupNotificator(messageDisplay);
        logNotificator = new LogNotificator(messageDisplay);
        emailNotificator = new EmailNotificator(new BasicMail(), messageDisplay);
    }

    public PopupNotificator getPopupNotificator() {
        return popupNotificator;
    }

    public LogNotificator getLogNotificator() {
        return logNotificator;
    }

    public EmailNotificator getEmailNotificator() {
        return emailNotificator;
    }
}
