package com.velox.sloan.workflows.notificator;


import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class NotificatorTest {
    private String requestId = "12345_C";

    @Test
    public void whenAddNoMessage_shouldDisplayNoMessages() {
        BulkNotificatorSpy errorNotificator = new BulkNotificatorSpy(new BulkNotificatorSpy.MyNotificator());

        errorNotificator.notifyAllMessages(requestId);

        assertThat(errorNotificator.getNotifiedMessage(), is(""));
    }

    @Test
    public void whenAddOneMessage_shouldDisplayOneMessage() {
        BulkNotificatorSpy errorNotificator = new BulkNotificatorSpy(new BulkNotificatorSpy.MyNotificator());
        String message = "My first sweet message";
        errorNotificator.addMessage(requestId, message);

        errorNotificator.notifyAllMessages(requestId);

        assertThat(errorNotificator.getNotifiedMessage(), is(message));
    }

    @Test
    public void whenAddTwoMessages_shouldDisplayConcatenatedMessages() {
        BulkNotificatorSpy errorNotificator = new BulkNotificatorSpy(new BulkNotificatorSpy.MyNotificator());
        String message1 = "My first sweet message";
        String message2 = "My second sweeter message";
        errorNotificator.addMessage(requestId, message1);
        errorNotificator.addMessage(requestId, message2);

        errorNotificator.notifyAllMessages(requestId);

        assertThat(errorNotificator.getNotifiedMessage(), is(String.format("%s%s", message1, message2)));
    }

    @Test
    public void whenAddMultipleMessages_shouldDisplayConcatenatedMessagesInOrder() {
        BulkNotificatorSpy errorNotificator = new BulkNotificatorSpy(new BulkNotificatorSpy.MyNotificator());
        String message1 = "My first sweet message";
        String message2 = "My second even sweeter message";
        String message3 = "My third sweetest message";
        String message4 = "My forth even sweeter than previous one message";
        errorNotificator.addMessage(requestId, message1);
        errorNotificator.addMessage(requestId, message2);
        errorNotificator.addMessage(requestId, message3);
        errorNotificator.addMessage(requestId, message4);

        errorNotificator.notifyAllMessages(requestId);

        assertThat(errorNotificator.getNotifiedMessage(), is(String.format("%s%s%s%s", message1, message2, message3, message4)));
    }

}