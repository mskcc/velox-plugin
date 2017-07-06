package com.velox.sloan.workflows.validator;

import com.velox.api.datarecord.DataRecord;
import com.velox.api.user.User;
import com.velox.sloan.workflows.notificator.Notificator;
import org.junit.Before;
import org.junit.Test;
import org.mskcc.domain.Request;

import java.util.Collections;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class AutoRunnabilityValidatorTest {
    private Notificator notificator = mock(Notificator.class);
    private AutoRunnabilityValidator autoRunnabilityValidator;
    private User user = mock(User.class);
    private Map<String, DataRecord> requestIdToRecord = Collections.EMPTY_MAP;

    @Before
    public void setUp() {
        autoRunnabilityValidator = new AutoRunnabilityValidator(notificator, user, requestIdToRecord);
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