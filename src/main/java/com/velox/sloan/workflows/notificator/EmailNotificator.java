package com.velox.sloan.workflows.notificator;

import com.velox.sloan.workflows.LoggerAndPopupDisplayer;
import com.velox.sloan.workflows.config.AppConfig;
import org.mskcc.util.BasicMail;

public class EmailNotificator implements Notificator {
    private final BasicMail basicMail;
    private final String from = "rezae@mskcc.org";
    private final String host = "cbio.mskcc.org";

    public EmailNotificator(BasicMail basicMail) {
        this.basicMail = basicMail;
    }

    @Override
    public void notifyMessage(String requestId, String message) {
        String title = String.format("Request: %s has issues which may result in validation errors: \n", requestId);
        String subject = String.format("Validation issues for request: %s", requestId);

        for (String emailAddress : AppConfig.getValidationErrorEmails()) {
            try {
                LoggerAndPopupDisplayer.logInfo(String.format("Sending email to: %s", emailAddress));
                basicMail.send(from, emailAddress, host, subject, title + message);
            } catch (Exception e) {
                LoggerAndPopupDisplayer.logError(String.format("Unable to send email from: %s to: %s using host: %s", from, emailAddress, host), e);
            }
        }
    }

    @Override
    public String getMessageSeparator() {
        return System.lineSeparator();
    }
}
