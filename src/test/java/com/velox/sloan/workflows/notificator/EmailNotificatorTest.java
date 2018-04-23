package com.velox.sloan.workflows.notificator;

import org.junit.Before;
import org.junit.Test;
import org.mskcc.util.email.Email;
import org.mskcc.util.email.EmailConfiguration;
import org.mskcc.util.email.EmailNotificator;
import org.mskcc.util.email.EmailSender;

import javax.mail.MessagingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class EmailNotificatorTest {
    private EmailNotificator emailNotificator;
    private EmailSenderSpy emailSenderSpy;

    @Before
    public void setUp() throws Exception {
        emailSenderSpy = new EmailSenderSpy();
    }

    @Test
    public void whenOneRecipent_shouldSendEmailToThisRecipientWithConfigurationGiven() throws Exception {
        List<String> recipients = Arrays.asList("bla@bla.bla");
        String from = "from@king.julian";
        String host = "sweet@host.ble";
        String message = "message to send";
        EmailConfiguration emailConfig = new EmailConfiguration(recipients, from, host);
        emailNotificator = new ValidatorEmailNotificator(emailSenderSpy, emailConfig);

        emailNotificator.notifyMessage("123", message);

        assertEmailSendToAllRecipients(recipients, from, host, message);
    }

    @Test
    public void whenMultipleRecipents_shouldSendEmailToAllRecipientsWithConfigurationGiven() throws Exception {
        List<String> recipients = Arrays.asList("bla@bla.bla", "other@diff.re", "tiruriru@tralala");
        String from = "from@king.julian";
        String host = "sweet@host.ble";
        EmailConfiguration emailConfig = new EmailConfiguration(recipients, from, host);
        emailNotificator = new ValidatorEmailNotificator(emailSenderSpy, emailConfig);

        String message = "very interesting message";
        emailNotificator.notifyMessage("123", message);

        assertEmailSendToAllRecipients(recipients, from, host, message);
    }

    private void assertEmailSendToAllRecipients(List<String> recipients, String from, String host, String message) {
        assertThat(emailSenderSpy.getEmails().size(), is(1));
        assertThat(emailSenderSpy.getEmails().get(0).getFrom(), is(from));
        assertThat(emailSenderSpy.getEmails().get(0).getRecipients(), is(recipients));
        assertThat(emailSenderSpy.getEmails().get(0).getHost(), is(host));
        assertThat(emailSenderSpy.getEmails().get(0).getMessage().contains(message), is(true));
    }

    private class EmailSenderSpy implements EmailSender {
        private List<Email> emails = new ArrayList<>();

        @Override
        public void send(Email emailToSend) throws MessagingException {
            emails.add(emailToSend);
        }

        public List<Email> getEmails() {
            return emails;
        }
    }
}