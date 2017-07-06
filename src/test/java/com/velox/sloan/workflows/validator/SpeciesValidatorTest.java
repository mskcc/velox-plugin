package com.velox.sloan.workflows.validator;

import com.velox.api.user.User;
import com.velox.sloan.workflows.notificator.Notificator;
import org.junit.Before;
import org.junit.Test;
import org.mskcc.domain.Request;

import java.util.Collections;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class SpeciesValidatorTest {
    private SpeciesValidator speciesValidator;
    private Request request;

    @Before
    public void setUp() {
        Notificator notificator = mock(Notificator.class);
        User user = mock(User.class);
        speciesValidator = new SpeciesValidator(notificator, user, Collections.emptyMap());
        request = new Request("12345_A");
    }

    @Test
    public void whenRequestHasNoSamples_shouldRequestBeValid() {
        boolean valid = speciesValidator.isValid(request);

        assertTrue(valid);
    }

    @Test
    public void whenRequestHasOneSample_shouldRequestBeValid() {
        request.putSampleIfAbsent(TestUtils.getHumanSample());

        boolean valid = speciesValidator.isValid(request);

        assertTrue(valid);
    }

    @Test
    public void whenRequestHasTwoHumanSamples_shouldRequestBeValid() {
        request.putSampleIfAbsent(TestUtils.getHumanSample());
        request.putSampleIfAbsent(TestUtils.getHumanSample());

        boolean valid = speciesValidator.isValid(request);

        assertTrue(valid);
    }

    @Test
    public void whenRequestHasOneHumanAndOneBacteriaSample_shouldRequestBeInValid() {
        request.putSampleIfAbsent(TestUtils.getHumanSample());
        request.putSampleIfAbsent(TestUtils.getChickenSample());

        boolean valid = speciesValidator.isValid(request);

        assertFalse(valid);
    }

    @Test
    public void whenRequestHasAllBacteriaAndOneOtherSample_shouldRequestBeInValid() {
        request.putSampleIfAbsent(TestUtils.getBacteriaSample());
        request.putSampleIfAbsent(TestUtils.getBacteriaSample());
        request.putSampleIfAbsent(TestUtils.getBacteriaSample());
        request.putSampleIfAbsent(TestUtils.getBacteriaSample());
        request.putSampleIfAbsent(TestUtils.getBacteriaSample());
        request.putSampleIfAbsent(TestUtils.getBacteriaSample());
        request.putSampleIfAbsent(TestUtils.getChickenSample());

        boolean valid = speciesValidator.isValid(request);

        assertFalse(valid);
    }

}