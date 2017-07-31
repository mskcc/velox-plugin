package com.velox.sloan.workflows.config;

import com.velox.sloan.workflows.LoggerAndPopupDisplayer;

import java.util.ArrayList;
import java.util.List;

public class AppConfig {
    private static List<String> validationErrorEmails = new ArrayList<>();

    public static void configure(ConfigurationSource configurationSource) {
        try {
            validationErrorEmails = configurationSource.getNotificationEmailAddresses();
        } catch (Exception e) {
            LoggerAndPopupDisplayer.logError("Unable to read validator configuration for email address. Email notifications won't be sent.");
        }
    }

    public static List<String> getValidationErrorEmails() {
        return validationErrorEmails;
    }
}
