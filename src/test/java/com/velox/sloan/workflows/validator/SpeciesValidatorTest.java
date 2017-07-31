package com.velox.sloan.workflows.validator;

import com.velox.sloan.workflows.notificator.BulkNotificator;
import org.junit.Before;
import org.junit.Test;
import org.mskcc.domain.Request;
import org.mskcc.domain.sample.Sample;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class SpeciesValidatorTest {
    private SpeciesValidator speciesValidator;
    private Request request;

    @Before
    public void setUp() {
        BulkNotificator notificator = mock(BulkNotificator.class);
        speciesValidator = new SpeciesValidator(notificator);
        request = new Request("12345_A");
    }

    @Test
    public void whenRequestHasOneSampleWithNoSpecies_shoulBeInvalid() {
        request.putSampleIfAbsent(new Sample("53454"));

        boolean valid = speciesValidator.isValid(request);

        assertFalse(valid);
    }

    @Test
    public void whenAllSamplesbutOneHaveSpecies_shoulBeInvalid() {
        request.putSampleIfAbsent(TestUtils.getBacteriaSample());
        request.putSampleIfAbsent(new Sample("53454"));
        request.putSampleIfAbsent(TestUtils.getBacteriaSample());
        request.putSampleIfAbsent(TestUtils.getBacteriaSample());
        request.putSampleIfAbsent(TestUtils.getBacteriaSample());

        boolean valid = speciesValidator.isValid(request);

        assertFalse(valid);
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