package com.velox.sloan.workflows.notificator;

import org.mskcc.util.email.EmailConfiguration;
import org.mskcc.util.email.EmailNotificator;
import org.mskcc.util.email.EmailSender;

public class ValidatorEmailNotificator extends EmailNotificator {
    public ValidatorEmailNotificator(EmailSender emailSender, EmailConfiguration emailConfiguration) {
        super(emailSender, emailConfiguration);
    }

    @Override
    protected String getFooter() {
        return String.format("\n\nPlease make sure these issues are resolved before sequencing completes. If they will not be, please warn the project managers (skicmopm@mskcc.org) and pipeline group (zzPDL_CMO_Prism@mskcc.org).");
    }

    @Override
    protected String getTitle(String requestId) {
        return String.format("Hello,\n\nRequest: %s has issues which may result in errors while generating manifest files: \n\n", requestId);
    }

    @Override
    public String getSubject(String requestId) {
        return String.format("Validation issues for request: %s", requestId);
    }
}
