package com.velox.sloan.workflows.validator;

import com.velox.sloan.workflows.notificator.BulkNotificator;
import org.junit.Before;
import org.junit.Test;
import org.mskcc.domain.Request;
import org.mskcc.domain.sample.Sample;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class SampleClassValidatorTest {
    public static final String TUMOR_CLASS = "Tumor class";
    public static final String NORMAL_CLASS = "Normal class";
    private final Sample validSample = new Sample("valid");
    private final Sample invalidSample = new Sample("invalid");
    private SampleClassValidator sampleClassValidator;
    private Request request;

    @Before
    public void setUp() throws Exception {
        sampleClassValidator = new SampleClassValidator(mock(BulkNotificator.class), s -> s.getIgoId().startsWith("valid"));
        request = new Request("12345_P");
    }

    @Test
    public void whenRequestHasNoSamples_shouldReturnValid() {
        boolean valid = sampleClassValidator.isValid(request);

        assertTrue(valid);
    }

    @Test
    public void whenRequestHasOneValidSample_shouldReturnValid() {
        request.putSampleIfAbsent(validSample);

        boolean valid = sampleClassValidator.isValid(request);

        assertTrue(valid);
    }

    @Test
    public void whenRequestHasOneValidAndOneInvalidSample_shouldReturnInvalid() {
        request.putSampleIfAbsent(validSample);
        request.putSampleIfAbsent(invalidSample);

        boolean valid = sampleClassValidator.isValid(request);

        assertFalse(valid);
    }

    @Test
    public void whenRequestHasMultipleValidSamples_shouldReturnValid() {
        request.putSampleIfAbsent(validSample);
        request.putSampleIfAbsent(validSample);
        request.putSampleIfAbsent(validSample);
        request.putSampleIfAbsent(validSample);

        boolean valid = sampleClassValidator.isValid(request);

        assertTrue(valid);
    }

    @Test
    public void whenRequestHasMultipleValidSamplesAndOneInvalidSample_shouldReturnInvalid() {
        request.putSampleIfAbsent(validSample);
        request.putSampleIfAbsent(validSample);
        request.putSampleIfAbsent(validSample);
        request.putSampleIfAbsent(validSample);
        request.putSampleIfAbsent(invalidSample);

        boolean valid = sampleClassValidator.isValid(request);

        assertFalse(valid);
    }



}