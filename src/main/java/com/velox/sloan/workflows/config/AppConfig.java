package com.velox.sloan.workflows.config;

import com.velox.sloan.workflows.LoggerAndPopupDisplayer;
import com.velox.sloan.workflows.notificator.EmailConfiguration;

import java.util.ArrayList;
import java.util.List;

public class AppConfig {
    private static String notificationEmailFrom = "rezae@mskcc.org";
    private static String notificationEmailHost = "cbio.mskcc.org";
    private static List<String> validationErrorEmails = new ArrayList<>();

    public static void configure(ConfigurationSource configurationSource) {
        try {
            validationErrorEmails = configurationSource.getNotificationEmailAddresses();
        } catch (Exception e) {
            LoggerAndPopupDisplayer.logError("Unable to read validator configuration for email address. Email notifications won't be sent.");
        }
    }

    public static EmailConfiguration getEmailConfiguration() {
        return new EmailConfiguration(validationErrorEmails, notificationEmailFrom, notificationEmailHost);
    }
}
