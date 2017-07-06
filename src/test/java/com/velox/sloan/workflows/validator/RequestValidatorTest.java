package com.velox.sloan.workflows.validator;

import com.velox.sapioutils.server.plugin.DefaultGenericPlugin;
import com.velox.sloan.workflows.notificator.MessageDisplay;
import com.velox.sloan.workflows.notificator.Notificator;
import com.velox.sloan.workflows.notificator.NotificatorSpy;
import org.junit.Before;
import org.junit.Test;
import org.mskcc.domain.Request;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class RequestValidatorTest {
    private Request requestMock = mock(Request.class);
    private NotificatorSpy errorNotificatorSpy;
    private String errorMessage = "Error message";
    private RequestValidator requestValidator = new RequestValidator();

    @Before
    public void setUp() {
        errorNotificatorSpy = new NotificatorSpy();
        LoggerAndPopup.configure(mock(MessageDisplay.class));
    }

    @Test
    public void whenNoValidatorsAdded_shouldReturnTrue() {
        boolean valid = requestValidator.isValid(requestMock);

        assertTrue(valid);
    }

    @Test
    public void whenOneValidValidatorsAdded_shouldReturnTrueAndNoErrorMessages() {
        requestValidator.addValidator(getValidValidatorMock());

        boolean valid = requestValidator.isValid(requestMock);

        assertTrue(valid);
        assertThat(errorNotificatorSpy.getAddedMessages().size(), is(0));
        assertThat(errorNotificatorSpy.getNotifiedMessage(), is(""));
    }

    @Test
    public void whenOneInvalidValidatorsAdded_shouldReturnFalseAndAddErrorMessage() {
        requestValidator.addValidator(getInvalidValidatorMock());

        boolean valid = requestValidator.isValid(requestMock);

        assertFalse(valid);
        assertThat(errorNotificatorSpy.getAddedMessages().size(), is(1));
        assertThat(errorNotificatorSpy.getAddedMessages().get(0), is(errorMessage));
        assertThat(errorNotificatorSpy.getNotifiedMessage(), is(errorMessage));
    }

    @Test
    public void whenTwoValidValidatorsAdded_shouldReturnTrueAndNoErrorMessages() {
        requestValidator.addValidator(getValidValidatorMock());
        requestValidator.addValidator(getValidValidatorMock());

        boolean valid = requestValidator.isValid(requestMock);

        assertTrue(valid);
        assertThat(errorNotificatorSpy.getAddedMessages().size(), is(0));
    }

    @Test
    public void whenMultipleValidValidatorsAdded_shouldReturnTrueAndNoErrorMessages() {
        requestValidator.addValidator(getValidValidatorMock());
        requestValidator.addValidator(getValidValidatorMock());
        requestValidator.addValidator(getValidValidatorMock());
        requestValidator.addValidator(getValidValidatorMock());

        boolean valid = requestValidator.isValid(requestMock);

        assertTrue(valid);
        assertThat(errorNotificatorSpy.getAddedMessages().size(), is(0));
    }

    @Test
    public void whenFourInvalidValidatorsAdded_shouldReturnFalseAndAddFourErrorMessages() {
        requestValidator.addValidator(getInvalidValidatorMock());
        requestValidator.addValidator(getInvalidValidatorMock());
        requestValidator.addValidator(getInvalidValidatorMock());
        requestValidator.addValidator(getInvalidValidatorMock());

        boolean valid = requestValidator.isValid(requestMock);

        assertFalse(valid);
        assertThat(errorNotificatorSpy.getAddedMessages().size(), is(4));
    }

    @Test
    public void whenFourInvalidAndOneValidValidatorsAdded_shouldReturnFalseAndAddErrorMessages() {
        requestValidator.addValidator(getInvalidValidatorMock());
        requestValidator.addValidator(getInvalidValidatorMock());
        requestValidator.addValidator(getInvalidValidatorMock());
        requestValidator.addValidator(getInvalidValidatorMock());
        requestValidator.addValidator(getValidValidatorMock());

        boolean valid = requestValidator.isValid(requestMock);

        assertFalse(valid);
        assertThat(errorNotificatorSpy.getAddedMessages().size(), is(4));
    }

    @Test
    public void whenMultipleValidAndOneInvalidValidatorsAdded_shouldReturnFalseAndAddErrorMessage() {
        requestValidator.addValidator(getInvalidValidatorMock());
        requestValidator.addValidator(getValidValidatorMock());
        requestValidator.addValidator(getValidValidatorMock());
        requestValidator.addValidator(getValidValidatorMock());
        requestValidator.addValidator(getValidValidatorMock());

        boolean valid = requestValidator.isValid(requestMock);

        assertFalse(valid);
        assertThat(errorNotificatorSpy.getAddedMessages().size(), is(1));
    }

    @Test
    public void whenOneValidAndOneInvalidValidatorsAdded_shouldReturnFalseAndAddOneErrorMessage() {
        requestValidator.addValidator(getValidValidatorMock());
        requestValidator.addValidator(getInvalidValidatorMock());

        boolean valid = requestValidator.isValid(requestMock);

        assertFalse(valid);
        assertThat(errorNotificatorSpy.getAddedMessages().size(), is(1));
    }

    @Test
    public void whenAllValidatorsAreValid_shouldNotNotifyAnyMessages() {
        NotificatorSpy notificatorSpy1 = new NotificatorSpy();
        requestValidator.addValidator(getValidatorMock(notificatorSpy1, true, "error1"));
        NotificatorSpy notificatorSpy2 = new NotificatorSpy();
        requestValidator.addValidator(getValidatorMock(notificatorSpy2, true, "error2"));

        boolean valid = requestValidator.isValid(requestMock);

        assertTrue(valid);
        assertThat(notificatorSpy1.getNotifiedMessage(), is(""));
        assertThat(notificatorSpy2.getNotifiedMessage(), is(""));

    }

    @Test
    public void whenOneValidatorIsInvalid_shouldNotifyItsNotificator() {
        NotificatorSpy notificatorSpy1 = new NotificatorSpy();
        String errorMessage = "error1";
        requestValidator.addValidator(getValidatorMock(notificatorSpy1, false, errorMessage));

        boolean valid = requestValidator.isValid(requestMock);

        assertFalse(valid);
        assertThat(notificatorSpy1.getNotifiedMessage(), is(errorMessage));
    }

    @Test
    public void whenOneValidatorIsInvalidAndOneValid_shouldNotifyInvalidNotificator() {
        NotificatorSpy notificatorSpy1 = new NotificatorSpy();
        NotificatorSpy notificatorSpy2 = new NotificatorSpy();
        String errorMessage1 = "error1";
        String errorMessage2 = "error2";
        requestValidator.addValidator(getValidatorMock(notificatorSpy1, false, errorMessage1));
        requestValidator.addValidator(getValidatorMock(notificatorSpy2, true, errorMessage2));

        boolean valid = requestValidator.isValid(requestMock);

        assertFalse(valid);
        assertThat(notificatorSpy1.getNotifiedMessage(), is(errorMessage1));
        assertThat(notificatorSpy2.getNotifiedMessage(), is(""));
    }

    @Test
    public void whenTwoValidatorAreInvalidUsingSameNotificator_shouldConcatMessagesInOneNotificator() {
        NotificatorSpy notificatorSpy1 = new NotificatorSpy();
        String errorMessage1 = "error1";
        String errorMessage2 = "error2";
        requestValidator.addValidator(getValidatorMock(notificatorSpy1, false, errorMessage1));
        requestValidator.addValidator(getValidatorMock(notificatorSpy1, false, errorMessage2));

        boolean valid = requestValidator.isValid(requestMock);

        assertFalse(valid);
        assertThat(notificatorSpy1.getNotifiedMessage(), is(errorMessage1+errorMessage2));
    }

    private Validator getValidatorMock(Notificator notificatorSpy, Boolean valid, String message) {
        Validator validValidatorMock = mock(Validator.class);
        when(validValidatorMock.isValid(any())).thenReturn(valid);
        when(validValidatorMock.getMessage(any())).thenReturn(message);
        when(validValidatorMock.getNotificator()).thenReturn(notificatorSpy);
        when(validValidatorMock.shouldValidate(any())).thenReturn(true);
        doCallRealMethod().when(validValidatorMock).addMessage(any());

        return validValidatorMock;
    }

    private Validator getValidValidatorMock() {
        Validator validValidatorMock = mock(Validator.class);
        when(validValidatorMock.isValid(any())).thenReturn(true);
        when(validValidatorMock.getNotificator()).thenReturn(errorNotificatorSpy);
        when(validValidatorMock.shouldValidate(any())).thenReturn(true);
        doCallRealMethod().when(validValidatorMock).addMessage(any());

        return validValidatorMock;
    }

    private Validator getInvalidValidatorMock() {
        Validator invalidValidatorMock = mock(Validator.class);
        when(invalidValidatorMock.isValid(any())).thenReturn(false);
        when(invalidValidatorMock.getNotificator()).thenReturn(errorNotificatorSpy);
        when(invalidValidatorMock.getMessage(any())).thenReturn(errorMessage);
        when(invalidValidatorMock.shouldValidate(any())).thenReturn(true);
        doCallRealMethod().when(invalidValidatorMock).addMessage(any());

        return invalidValidatorMock;
    }

}