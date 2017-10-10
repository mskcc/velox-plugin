package com.velox.sloan.workflows.notificator;

import org.junit.Before;
import org.junit.Test;
import org.mskcc.util.EmailConfiguration;
import org.mskcc.util.EmailSender;

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
    public void whenOneRecipent_shouldSendEmailToThisRecipientWithConfigurationGiven() {
        List<String> recipients = Arrays.asList("bla@bla.bla");
        String from = "from@king.julian";
        String host = "sweet@host.ble";
        String message = "message to send";
        EmailConfiguration emailConfig = new EmailConfiguration(recipients, from, host);
        emailNotificator = new EmailNotificator(emailSenderSpy, emailConfig);

        emailNotificator.notifyMessage("123", message);

        assertEmailSendToAllRecipients(recipients, from, host, message);
    }

    @Test
    public void whenMultipleRecipents_shouldSendEmailToAllRecipientsWithConfigurationGiven() {
        List<String> recipients = Arrays.asList("bla@bla.bla", "other@diff.re", "tiruriru@tralala");
        String from = "from@king.julian";
        String host = "sweet@host.ble";
        EmailConfiguration emailConfig = new EmailConfiguration(recipients, from, host);
        emailNotificator = new EmailNotificator(emailSenderSpy, emailConfig);

        String message = "very interesting message";
        emailNotificator.notifyMessage("123", message);

        assertEmailSendToAllRecipients(recipients, from, host, message);
    }

    private void assertEmailSendToAllRecipients(List<String> recipients, String from, String host, String message) {
        assertThat(emailSenderSpy.getEmails().size(), is(recipients.size()));
        for (int i = 0; i < recipients.size(); i++) {
            assertThat(emailSenderSpy.getEmails().get(i).from, is(from));
            assertThat(emailSenderSpy.getEmails().get(i).to, is(recipients.get(i)));
            assertThat(emailSenderSpy.getEmails().get(i).host, is(host));
            assertThat(emailSenderSpy.getEmails().get(i).text.contains(message), is(true));
        }
    }

    private class EmailSenderSpy implements EmailSender {
        private List<Email> emails = new ArrayList<>();

        @Override
        public void send(String from, String to, String host, String subject, String text) throws MessagingException {
            Email email = new Email(from, to, host, subject, text);
            emails.add(email);
        }

        @Override
        public void sendWithFiles(String from, String to, String host, String subject, String text, String files) throws MessagingException {
        }

        public List<Email> getEmails() {
            return emails;
        }

        class Email {
            private String from;
            private String to;
            private String host;
            private String subject;
            private String text;

            Email(String from, String to, String host, String subject, String text) {
                this.from = from;
                this.to = to;
                this.host = host;
                this.subject = subject;
                this.text = text;
            }
        }
    }
}