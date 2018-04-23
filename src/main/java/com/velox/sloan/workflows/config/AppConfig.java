package com.velox.sloan.workflows.config;

import com.velox.sloan.workflows.LoggerAndPopupDisplayer;
import org.mskcc.util.email.EmailConfiguration;

import java.util.ArrayList;
import java.util.List;

public class AppConfig {
    private static final AppProperties appProperties = new AppProperties();
    private static List<String> validationErrorEmails = new ArrayList<>();

    public static void configure(ConfigurationSource configurationSource) {
        appProperties.configure();

        try {
            validationErrorEmails = configurationSource.getNotificationEmailAddresses();
        } catch (Exception e) {
            LoggerAndPopupDisplayer.logError("Unable to read validator configuration for email address. Email notifications won't be sent.");
        }
    }

    public static EmailConfiguration getEmailConfiguration() {
        return new EmailConfiguration(validationErrorEmails, appProperties.getNotificationEmailFrom(), appProperties.getNotificationEmailHost());
    }
}
