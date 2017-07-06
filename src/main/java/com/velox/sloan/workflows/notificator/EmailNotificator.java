package com.velox.sloan.workflows.notificator;

import org.mskcc.util.BasicMail;

public class EmailNotificator extends Notificator {
    private final BasicMail basicMail;
    private final MessageDisplay messageDisplay;
    private final String from = "rezae@mskcc.org";
    private final String to = "rezae@mskcc.org";
    private final String host = "cbio.mskcc.org";

    public EmailNotificator(BasicMail basicMail, MessageDisplay messageDisplay) {
        this.basicMail = basicMail;
        this.messageDisplay = messageDisplay;
    }

    @Override
    public void notifyMessage(String requestId, String message) {
        String title = String.format("Request: %s issues: \n", requestId);
        String subject = String.format("Issues with request: %s", requestId);

        try {
            basicMail.send(from, to, host, subject, title + message);
        } catch (Exception e) {
            messageDisplay.logErrorMessage(String.format("Unable to send email from: %s to: %s using host: %s", from, to, host));
        }
    }

    @Override
    public String getMessageSeparator() {
        return System.lineSeparator();
    }
}
