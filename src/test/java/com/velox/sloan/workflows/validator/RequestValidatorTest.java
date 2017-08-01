package com.velox.sloan.workflows.validator;

import com.velox.sloan.workflows.LoggerAndPopupDisplayer;
import com.velox.sloan.workflows.notificator.BulkNotificator;
import com.velox.sloan.workflows.notificator.BulkNotificatorSpy;
import com.velox.sloan.workflows.notificator.MessageDisplay;
import org.junit.Before;
import org.junit.Test;
import org.mskcc.domain.Request;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class RequestValidatorTest {
    private final BulkNotificatorSpy.MyNotificator mockNotificator = new BulkNotificatorSpy.MyNotificator();
    private Request requestMock = mock(Request.class);
    private BulkNotificatorSpy errorBulkNotificatorSpy;
    private String errorMessage = "Error message";
    private RequestValidator requestValidator = new RequestValidator();

    @Before
    public void setUp() {
        errorBulkNotificatorSpy = new BulkNotificatorSpy(mockNotificator);
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
        assertThat(errorBulkNotificatorSpy.getAddedMessages().size(), is(0));
        assertThat(errorBulkNotificatorSpy.getNotifiedMessage(), is(""));
    }

    @Test
    public void whenOneInvalidValidatorsAdded_shouldReturnFalseAndAddErrorMessage() {
        requestValidator.addValidator(getInvalidValidatorMock());

        boolean valid = requestValidator.isValid(requestMock);

        assertFalse(valid);
        assertThat(errorBulkNotificatorSpy.getAddedMessages().size(), is(1));
        assertThat(errorBulkNotificatorSpy.getAddedMessages().get(0), is(errorMessage));
        assertThat(errorBulkNotificatorSpy.getNotifiedMessage(), is(errorMessage));
    }

    @Test
    public void whenTwoValidValidatorsAdded_shouldReturnTrueAndNoErrorMessages() {
        requestValidator.addValidator(getValidValidatorMock());
        requestValidator.addValidator(getValidValidatorMock());

        boolean valid = requestValidator.isValid(requestMock);

        assertTrue(valid);
        assertThat(errorBulkNotificatorSpy.getAddedMessages().size(), is(0));
    }

    @Test
    public void whenMultipleValidValidatorsAdded_shouldReturnTrueAndNoErrorMessages() {
        requestValidator.addValidator(getValidValidatorMock());
        requestValidator.addValidator(getValidValidatorMock());
        requestValidator.addValidator(getValidValidatorMock());
        requestValidator.addValidator(getValidValidatorMock());

        boolean valid = requestValidator.isValid(requestMock);

        assertTrue(valid);
        assertThat(errorBulkNotificatorSpy.getAddedMessages().size(), is(0));
    }

    @Test
    public void whenFourInvalidValidatorsAdded_shouldReturnFalseAndAddFourErrorMessages() {
        requestValidator.addValidator(getInvalidValidatorMock());
        requestValidator.addValidator(getInvalidValidatorMock());
        requestValidator.addValidator(getInvalidValidatorMock());
        requestValidator.addValidator(getInvalidValidatorMock());

        boolean valid = requestValidator.isValid(requestMock);

        assertFalse(valid);
        assertThat(errorBulkNotificatorSpy.getAddedMessages().size(), is(4));
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
        assertThat(errorBulkNotificatorSpy.getAddedMessages().size(), is(4));
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
        assertThat(errorBulkNotificatorSpy.getAddedMessages().size(), is(1));
    }

    @Test
    public void whenOneValidAndOneInvalidValidatorsAdded_shouldReturnFalseAndAddOneErrorMessage() {
        requestValidator.addValidator(getValidValidatorMock());
        requestValidator.addValidator(getInvalidValidatorMock());

        boolean valid = requestValidator.isValid(requestMock);

        assertFalse(valid);
        assertThat(errorBulkNotificatorSpy.getAddedMessages().size(), is(1));
    }

    @Test
    public void whenAllValidatorsAreValid_shouldNotNotifyAnyMessages() {
        BulkNotificatorSpy bulkNotificatorSpy1 = new BulkNotificatorSpy(mockNotificator);
        requestValidator.addValidator(getValidatorMock(bulkNotificatorSpy1, true, "error1"));
        BulkNotificatorSpy bulkNotificatorSpy2 = new BulkNotificatorSpy(mockNotificator);
        requestValidator.addValidator(getValidatorMock(bulkNotificatorSpy2, true, "error2"));

        boolean valid = requestValidator.isValid(requestMock);

        assertTrue(valid);
        assertThat(bulkNotificatorSpy1.getNotifiedMessage(), is(""));
        assertThat(bulkNotificatorSpy2.getNotifiedMessage(), is(""));

    }

    @Test
    public void whenOneValidatorIsInvalid_shouldNotifyItsNotificator() {
        BulkNotificatorSpy bulkNotificatorSpy1 = new BulkNotificatorSpy(mockNotificator);
        String errorMessage = "error1";
        requestValidator.addValidator(getValidatorMock(bulkNotificatorSpy1, false, errorMessage));

        boolean valid = requestValidator.isValid(requestMock);

        assertFalse(valid);
        assertThat(bulkNotificatorSpy1.getNotifiedMessage(), is(errorMessage));
    }

    @Test
    public void whenOneValidatorIsInvalidAndOneValid_shouldNotifyInvalidNotificator() {
        BulkNotificatorSpy bulkNotificatorSpy1 = new BulkNotificatorSpy(mockNotificator);
        BulkNotificatorSpy bulkNotificatorSpy2 = new BulkNotificatorSpy(new BulkNotificatorSpy.MyNotificator());
        String errorMessage1 = "error1";
        String errorMessage2 = "error2";
        requestValidator.addValidator(getValidatorMock(bulkNotificatorSpy1, false, errorMessage1));
        requestValidator.addValidator(getValidatorMock(bulkNotificatorSpy2, true, errorMessage2));

        boolean valid = requestValidator.isValid(requestMock);

        assertFalse(valid);
        assertThat(bulkNotificatorSpy1.getNotifiedMessage(), is(errorMessage1));
        assertThat(bulkNotificatorSpy2.getNotifiedMessage(), is(""));
    }

    @Test
    public void whenTwoValidatorAreInvalidUsingSameNotificator_shouldConcatMessagesInOneNotificator() {
        BulkNotificatorSpy bulkNotificatorSpy1 = new BulkNotificatorSpy(mockNotificator);
        String errorMessage1 = "error1";
        String errorMessage2 = "error2";
        requestValidator.addValidator(getValidatorMock(bulkNotificatorSpy1, false, errorMessage1));
        requestValidator.addValidator(getValidatorMock(bulkNotificatorSpy1, false, errorMessage2));

        boolean valid = requestValidator.isValid(requestMock);

        assertFalse(valid);
        assertThat(bulkNotificatorSpy1.getNotifiedMessage(), is(errorMessage1 + errorMessage2));
        assertThat(bulkNotificatorSpy1.getReqIdToNotifyCounter().values().stream().anyMatch(counter -> counter == 1), is(true));
    }

    private Validator getValidatorMock(BulkNotificatorSpy bulkNotificatorSpy, Boolean valid, String message) {
        Validator validator = new Validator() {
            @Override
            public boolean isValid(Request request) {
                return valid;
            }

            @Override
            public BulkNotificator getBulkNotificator() {
                return bulkNotificatorSpy;
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
        return getValidatorMock(errorBulkNotificatorSpy, true, "");
    }

    private Validator getInvalidValidatorMock() {
        return getValidatorMock(errorBulkNotificatorSpy, false, errorMessage);
    }
}