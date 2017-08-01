package com.velox.sloan.workflows.notificator;

import com.velox.sloan.workflows.config.AppConfig;
import org.mskcc.util.JavaxEmailSender;

import java.util.Arrays;

public class NotificatorFactory {
    private final PopupNotificator popupNotificator;
    private final LogNotificator logNotificator;
    private final EmailNotificator emailNotificator;

    private BulkNotificator emailBulkNotificator;
    private BulkNotificator logBulkNotificator;
    private BulkNotificator popupBulkNotificator;

    public NotificatorFactory(MessageDisplay messageDisplay) {
        popupNotificator = new PopupNotificator(messageDisplay);
        logNotificator = new LogNotificator(messageDisplay);
        emailNotificator = new EmailNotificator(new JavaxEmailSender(), AppConfig.getEmailConfiguration());
    }

    public BulkNotificator getPopupNotificator() {
        if (popupBulkNotificator == null)
            popupBulkNotificator = new BulkNotificator(Arrays.asList(popupNotificator, logNotificator));
        return popupBulkNotificator;
    }

    public BulkNotificator getLogNotificator() {
        if (logBulkNotificator == null)
            logBulkNotificator = new BulkNotificator(Arrays.asList(logNotificator));
        return logBulkNotificator;
    }

    public BulkNotificator getEmailNotificator() {
        if (emailBulkNotificator == null)
            emailBulkNotificator = new BulkNotificator(Arrays.asList(emailNotificator, logNotificator));
        return emailBulkNotificator;
    }
}
