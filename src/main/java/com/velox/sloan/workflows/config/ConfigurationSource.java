package com.velox.sloan.workflows.config;

import java.util.List;

public interface ConfigurationSource {
    List<String> getNotificationEmailAddresses() throws Exception;
}
