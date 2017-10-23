package com.velox.sloan.workflows.config;

import com.velox.sloan.workflows.LoggerAndPopupDisplayer;

import java.io.InputStream;

public class AppProperties {
    private String notificationEmailFrom = "zzPDL_SKI_Lims_Request@mskcc.org";
    private String notificationEmailHost = "cbio.mskcc.org";

    public void configure() {
        String propertiesFileName = "app.properties";

        try (InputStream input = AppProperties.class.getClassLoader().getResourceAsStream((propertiesFileName))) {
            java.util.Properties prop = new java.util.Properties();
            prop.load(input);
            notificationEmailFrom = prop.getProperty("notificationEmailFrom");
            notificationEmailHost = prop.getProperty("notificationEmailHost");
        } catch (Exception ex) {
            LoggerAndPopupDisplayer.logInfo(String.format("Unable to read properties file: %s. Default values will be used", propertiesFileName));
        }
    }

    public String getNotificationEmailFrom() {
        return notificationEmailFrom;
    }

    public String getNotificationEmailHost() {
        return notificationEmailHost;
    }
}
