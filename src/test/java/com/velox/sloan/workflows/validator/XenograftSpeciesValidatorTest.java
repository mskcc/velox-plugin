package com.velox.sloan.workflows.validator;

import com.velox.api.user.User;
import com.velox.sloan.workflows.notificator.Notificator;
import org.junit.Before;
import org.junit.Test;
import org.mskcc.domain.Request;
import org.mskcc.domain.Sample;

import java.util.Collections;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class XenograftSpeciesValidatorTest {
    private XenograftSpeciesValidator xenograftSpeciesValidator;
    private final Request request = new Request("12345_P");

    @Before
    public void setUp() {
        xenograftSpeciesValidator = new XenograftSpeciesValidator(mock(Notificator.class), mock(User.class), Collections.EMPTY_MAP);
    }

    @Test
    public void whenRequestHasOneXenograftSample_shouldRunValidator() {
        Sample sample = TestUtils.getXenograftSample();
        request.putSampleIfAbsent(sample);

        boolean shouldValidate = xenograftSpeciesValidator.shouldValidate(request);

        assertTrue(shouldValidate);
    }

    @Test
    public void whenRequestHasOneXenograftAndOneNonXenograftSample_shouldRunValidator() {
        Sample xenograftSample = TestUtils.getXenograftSample();
        Sample nonXenograftSample = TestUtils.getNonXenograftSample();
        request.putSampleIfAbsent(xenograftSample);
        request.putSampleIfAbsent(nonXenograftSample);

        boolean shouldValidate = xenograftSpeciesValidator.shouldValidate(request);

        assertTrue(shouldValidate);
    }

    @Test
    public void whenRequestHasOnlyNonXenograftSamples_shouldRunNotValidator() {
        request.putSampleIfAbsent(TestUtils.getNonXenograftSample());
        request.putSampleIfAbsent(TestUtils.getNonXenograftSample());
        request.putSampleIfAbsent(TestUtils.getNonXenograftSample());

        boolean shouldValidate = xenograftSpeciesValidator.shouldValidate(request);

        assertFalse(shouldValidate);
    }

    @Test
    public void whenXenograftRequestHasOnlyOneHumanSample_shouldRequestBeValid() {
        request.putSampleIfAbsent(TestUtils.getHumanSample());

        boolean valid = xenograftSpeciesValidator.isValid(request);

        assertTrue(valid);
    }

    @Test
    public void whenXenograftRequestHasOnlyOneXenograftSample_shouldRequestBeValid() {
        request.putSampleIfAbsent(TestUtils.getXenograftSample());

        boolean valid = xenograftSpeciesValidator.isValid(request);

        assertTrue(valid);
    }

    @Test
    public void whenXenograftRequestHasOnlyHumanNadXenograftSample_shouldRequestBeValid() {
        request.putSampleIfAbsent(TestUtils.getXenograftSample());
        request.putSampleIfAbsent(TestUtils.getHumanSample());
        request.putSampleIfAbsent(TestUtils.getXenograftSample());
        request.putSampleIfAbsent(TestUtils.getXenograftSample());

        boolean valid = xenograftSpeciesValidator.isValid(request);

        assertTrue(valid);
    }

    @Test
    public void whenXenograftRequestHasOnlyOneNonHumanNonXenograftSample_shouldRequestBeInValid() {
        request.putSampleIfAbsent(TestUtils.getBacteriaSample());

        boolean valid = xenograftSpeciesValidator.isValid(request);

        assertFalse(valid);
    }

    @Test
    public void whenXenograftRequestHasMultipleNonHumanNonXenograftSample_shouldRequestBeInValid() {
        request.putSampleIfAbsent(TestUtils.getBacteriaSample());
        request.putSampleIfAbsent(TestUtils.getChickenSample());
        request.putSampleIfAbsent(TestUtils.getChickenSample());

        boolean valid = xenograftSpeciesValidator.isValid(request);

        assertFalse(valid);
    }

    @Test
    public void whenXenograftRequestHasHumanAndBacteriaSample_shouldRequestBeInValid() {
        request.putSampleIfAbsent(TestUtils.getBacteriaSample());
        request.putSampleIfAbsent(TestUtils.getHumanSample());

        boolean valid = xenograftSpeciesValidator.isValid(request);

        assertFalse(valid);
    }

}