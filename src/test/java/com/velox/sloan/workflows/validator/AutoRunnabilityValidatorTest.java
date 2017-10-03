package com.velox.sloan.workflows.validator;

import com.velox.sloan.workflows.notificator.BulkNotificator;
import org.junit.Before;
import org.junit.Test;
import org.mskcc.domain.Request;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class AutoRunnabilityValidatorTest {
    private BulkNotificator notificator = mock(BulkNotificator.class);
    private AutoRunnabilityValidator autoRunnabilityValidator;

    @Before
    public void setUp() {
        autoRunnabilityValidator = new AutoRunnabilityValidator(notificator);
    }

    @Test
    public void whenRequestIsAutogenerable_shouldReturnValid() {
        Request autogenerableRequest = new Request("12345_B");
        autogenerableRequest.setBicAutorunnable(true);

        boolean valid = autoRunnabilityValidator.isValid(autogenerableRequest);

        assertTrue(valid);
    }

    @Test
    public void whenRequestIsNotAutogenerable_shouldReturnNotValid() {
        Request autogenerableRequest = new Request("12345_B");
        autogenerableRequest.setBicAutorunnable(false);

        boolean valid = autoRunnabilityValidator.isValid(autogenerableRequest);

        assertFalse(valid);
    }
}