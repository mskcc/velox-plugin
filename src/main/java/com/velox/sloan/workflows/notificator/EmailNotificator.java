package com.velox.sloan.workflows.notificator;

import com.velox.sloan.workflows.LoggerAndPopupDisplayer;
import org.mskcc.util.EmailConfiguration;
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
        String title = String.format("Hello,\n\nRequest: %s has issues which may result in errors while generating manifest files: \n\n", requestId);
        String subject = String.format("Validation issues for request: %s", requestId);
        String footer = String.format("\n\nPlease make sure these issues are resolved before sequencing completes. If they will not be, please warn the project managers (skicmopm@mskcc.org) and pipeline group (zzPDL_CMO_Prism@mskcc.org).");

        for (String recipient : emailConfiguration.getRecipients()) {
            try {
                LoggerAndPopupDisplayer.logInfo(String.format("Sending email to: %s", recipient));
                emailSender.send(emailConfiguration.getFrom(), recipient, emailConfiguration.getHost(), subject, title + message + footer);
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
