package com.velox.sloan.workflows.validator;

import com.velox.sloan.workflows.LoggerAndPopupDisplayer;
import com.velox.sloan.workflows.notificator.BulkNotificator;
import com.velox.sloan.workflows.notificator.MessageDisplay;
import com.velox.sloan.workflows.notificator.NotificatorSpy;
import org.junit.Before;
import org.junit.Test;
import org.mskcc.domain.Request;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class RequestValidatorTest {
    private final NotificatorSpy.MyNotificator mockNotificator = new NotificatorSpy.MyNotificator();
    private Request requestMock = mock(Request.class);
    private NotificatorSpy errorNotificatorSpy;
    private String errorMessage = "Error message";
    private RequestValidator requestValidator = new RequestValidator();

    @Before
    public void setUp() {
        errorNotificatorSpy = new NotificatorSpy(mockNotificator);
        LoggerAndPopupDisplayer.configure(mock(MessageDisplay.class));
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
        NotificatorSpy notificatorSpy1 = new NotificatorSpy(mockNotificator);
        requestValidator.addValidator(getValidatorMock(notificatorSpy1, true, "error1"));
        NotificatorSpy notificatorSpy2 = new NotificatorSpy(mockNotificator);
        requestValidator.addValidator(getValidatorMock(notificatorSpy2, true, "error2"));

        boolean valid = requestValidator.isValid(requestMock);

        assertTrue(valid);
        assertThat(notificatorSpy1.getNotifiedMessage(), is(""));
        assertThat(notificatorSpy2.getNotifiedMessage(), is(""));

    }

    @Test
    public void whenOneValidatorIsInvalid_shouldNotifyItsNotificator() {
        NotificatorSpy notificatorSpy1 = new NotificatorSpy(mockNotificator);
        String errorMessage = "error1";
        requestValidator.addValidator(getValidatorMock(notificatorSpy1, false, errorMessage));

        boolean valid = requestValidator.isValid(requestMock);

        assertFalse(valid);
        assertThat(notificatorSpy1.getNotifiedMessage(), is(errorMessage));
    }

    @Test
    public void whenOneValidatorIsInvalidAndOneValid_shouldNotifyInvalidNotificator() {
        NotificatorSpy notificatorSpy1 = new NotificatorSpy(mockNotificator);
        NotificatorSpy notificatorSpy2 = new NotificatorSpy(new NotificatorSpy.MyNotificator());
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
        NotificatorSpy notificatorSpy1 = new NotificatorSpy(mockNotificator);
        String errorMessage1 = "error1";
        String errorMessage2 = "error2";
        requestValidator.addValidator(getValidatorMock(notificatorSpy1, false, errorMessage1));
        requestValidator.addValidator(getValidatorMock(notificatorSpy1, false, errorMessage2));

        boolean valid = requestValidator.isValid(requestMock);

        assertFalse(valid);
        assertThat(notificatorSpy1.getNotifiedMessage(), is(errorMessage1+errorMessage2));
        assertThat(notificatorSpy1.getReqIdToNotifyCounter().values().stream().anyMatch(counter -> counter == 1), is(true));
    }

    private Validator getValidatorMock(NotificatorSpy notificatorSpy, Boolean valid, String message) {
        Validator validator = new Validator() {
            @Override
            public boolean isValid(Request request) {
                return valid;
            }

            @Override
            public BulkNotificator getBulkNotificator() {
                return notificatorSpy;
            }

            @Override
            public String getMessage(Request request) {
                return message;
            }

            @Override
            public String getName() {
                return "";
            }

            @Override
            public boolean shouldValidate(Request request) {
                return true;
            }
        };

        return validator;
    }

    private Validator getValidValidatorMock() {
        return getValidatorMock(errorNotificatorSpy, true, "");
    }

    private Validator getInvalidValidatorMock() {
        return getValidatorMock(errorNotificatorSpy, false, errorMessage);
    }
}