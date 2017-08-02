package com.velox.sloan.workflows.notificator;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

public class NotificatorFactoryTest {
    private NotificatorFactory notificatorFactory;

    @Before
    public void setUp() throws Exception {
        notificatorFactory = new NotificatorFactory(mock(MessageDisplay.class));
    }

    @Test
    public void whenEmailNotificatorIsRetreivedMultipleTimes_shouldReturnAlwaysSameInstance() {
        BulkNotificator emailNotificator = notificatorFactory.getEmailNotificator();
        BulkNotificator emailNotificator2 = notificatorFactory.getEmailNotificator();
        BulkNotificator emailNotificator3 = notificatorFactory.getEmailNotificator();
        BulkNotificator emailNotificator4 = notificatorFactory.getEmailNotificator();

        assertThat(emailNotificator == emailNotificator2, is(true));
        assertThat(emailNotificator2 == emailNotificator3, is(true));
        assertThat(emailNotificator3 == emailNotificator4, is(true));
    }

    @Test
    public void whenLoglNotificatorIsRetreivedMultipleTimes_shouldReturnAlwaysSameInstance() {
        BulkNotificator logNotificator = notificatorFactory.getLogNotificator();
        BulkNotificator logNotificator1 = notificatorFactory.getLogNotificator();

        assertThat(logNotificator == logNotificator1, is(true));
    }

    @Test
    public void whenPopuplNotificatorIsRetreivedMultipleTimes_shouldReturnAlwaysSameInstance() {
        BulkNotificator popupNotificator = notificatorFactory.getPopupNotificator();
        BulkNotificator popupNotificator1 = notificatorFactory.getPopupNotificator();

        assertThat(popupNotificator == popupNotificator1, is(true));
    }
}