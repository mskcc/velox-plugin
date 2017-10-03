package com.velox.sloan.workflows.config;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class AppPropertiesTest {
    private AppProperties appProperties = new AppProperties();

    @Test
    public void when_should() {
        appProperties.configure();

        assertThat(appProperties.getNotificationEmailFrom(), is("test@mskcc.org"));
        assertThat(appProperties.getNotificationEmailHost(), is("test.mskcc.org"));
    }

}