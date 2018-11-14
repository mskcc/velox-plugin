package com.velox.sloan.workflows.notificator;

import org.junit.Before;
import org.junit.Test;
import org.mskcc.util.email.*;

import javax.mail.MessagingException;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class EmailNotificatorTest {
    private EmailNotificator emailNotificator;
    private EmailSender emailSender;

    @Before
    public void setUp() throws Exception {
        emailSender = new EmailSenderSpy();
    }

    @Test
    public void whenOneRecipent_shouldSendEmailToThisRecipientWithConfigurationGiven() {
        List<String> recipients = Arrays.asList("bla@bla.bla");
        String from = "from@king.julian";
        String host = "sweet@host.ble";
        String message = "message to send";
        EmailConfiguration emailConfig = new EmailConfiguration(recipients, from, host);
        emailNotificator = new EmailNotificator(emailSender, emailConfig);

        emailNotificator.notifyMessage("123", message);

        assertEmailSendToAllRecipients(recipients, from, host, message);
    }

    @Test
    public void whenMultipleRecipents_shouldSendEmailToAllRecipientsWithConfigurationGiven() {
        List<String> recipients = Arrays.asList("bla@bla.bla", "other@diff.re", "tiruriru@tralala");
        String from = "from@king.julian";
        String host = "sweet@host.ble";
        EmailConfiguration emailConfig = new EmailConfiguration(recipients, from, host);
        emailNotificator = new EmailNotificator(emailSender, emailConfig);

        String message = "very interesting message";
        emailNotificator.notifyMessage("123", message);

        assertEmailSendToAllRecipients(recipients, from, host, message);
    }

    private void assertEmailSendToAllRecipients(List<String> recipients, String from, String host, String message) {
        EmailSenderSpy emailSenderSpy = (EmailSenderSpy) emailSender;
        assertThat(emailSenderSpy.getEmail().getFrom(), is(from));
        assertThat(emailSenderSpy.getEmail().getRecipients(), is(recipients));
        assertThat(emailSenderSpy.getEmail().getHost(), is(host));
        assertThat(emailSenderSpy.getEmail().getMessage(), is(message));
    }

    private class EmailSenderSpy implements EmailSender {

        private Email email;

        @Override
        public void send(Email email) throws MessagingException {
            this.email = email;
        }

        public Email getEmail() {
            return email;
        }
    }
}