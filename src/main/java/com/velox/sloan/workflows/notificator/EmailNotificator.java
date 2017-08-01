package com.velox.sloan.workflows.notificator;

import com.velox.sloan.workflows.LoggerAndPopupDisplayer;
import org.mskcc.util.EmailSender;

public class EmailNotificator implements Notificator {
    private final EmailSender emailSender;
    private final EmailConfiguration emailConfiguration;

    public EmailNotificator(EmailSender emailSender, EmailConfiguration emailConfiguration) {
        this.emailSender = emailSender;
        this.emailConfiguration = emailConfiguration;
    }

    @Override
    public void notifyMessage(String requestId, String message) {
        String title = String.format("Request: %s has issues which may result in errors while generating manifest files: \n", requestId);
        String subject = String.format("Validation issues for request: %s", requestId);

        for (String recipient : emailConfiguration.getRecipients()) {
            try {
                LoggerAndPopupDisplayer.logInfo(String.format("Sending email to: %s", recipient));
                emailSender.send(emailConfiguration.getFrom(), recipient, emailConfiguration.getHost(), subject, title + message);
            } catch (Exception e) {
                LoggerAndPopupDisplayer.logError(String.format("Unable to send email from: %s to: %s using host: %s", emailConfiguration.getFrom(), recipient, emailConfiguration.getHost()), e);
            }
        }
    }

    @Override
    public String getMessageSeparator() {
        return System.lineSeparator();
    }
}
