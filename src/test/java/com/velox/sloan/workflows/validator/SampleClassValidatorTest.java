package com.velox.sloan.workflows.validator;

import com.velox.sloan.workflows.notificator.BulkNotificator;
import org.junit.Before;
import org.junit.Test;
import org.mskcc.domain.Request;
import org.mskcc.domain.sample.CmoSampleInfo;
import org.mskcc.domain.sample.Sample;
import org.mskcc.domain.sample.TumorNormalType;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class SampleClassValidatorTest {
    public static final String TUMOR_CLASS = "Tumor class";
    public static final String NORMAL_CLASS = "Normal class";
    private SampleClassValidator sampleClassValidator;
    private Request request;
    private int sampleCounter = 0;

    @Before
    public void setUp() throws Exception {
        sampleClassValidator = new SampleClassValidator(mock(BulkNotificator.class));
        request = new Request("12345_P");
    }

    @Test
    public void whenRequestHasNoSamples_shouldReturnValid() {
        boolean valid = sampleClassValidator.isValid(request);

        assertTrue(valid);
    }

    @Test
    public void whenRequestHasOneTumorClassAndTypeSampleWithNoSampleInfo_shouldReturnValid() {
        request.putSampleIfAbsent(getSample(TUMOR_CLASS, TumorNormalType.TUMOR, "", null));

        boolean valid = sampleClassValidator.isValid(request);

        assertTrue(valid);
    }

    @Test
    public void whenRequestHasOneTumorClassAndTypeSampleWithTumorClassAndTypeSampleInfo_shouldReturnValid() {
        Sample sample = getSample(TUMOR_CLASS, TumorNormalType.TUMOR, TUMOR_CLASS, TumorNormalType.TUMOR);
        request.putSampleIfAbsent(sample);

        boolean valid = sampleClassValidator.isValid(request);

        assertTrue(valid);
    }

    @Test
    public void whenRequestHasOneTumorClassAndTypeSampleWithSampleTumorClassNormalTypeInfo_shouldReturnInvalid() {
        Sample sample = getSample(TUMOR_CLASS, TumorNormalType.TUMOR, TUMOR_CLASS, TumorNormalType.NORMAL);
        request.putSampleIfAbsent(sample);

        boolean valid = sampleClassValidator.isValid(request);

        assertFalse(valid);
    }

    @Test
    public void whenRequestHasOneTumorClassAndTypeSampleWithSampleNormalClassTumorTypeInfo_shouldReturnInvalid() {
        Sample sample = getSample(TUMOR_CLASS, TumorNormalType.TUMOR, NORMAL_CLASS, TumorNormalType.TUMOR);
        request.putSampleIfAbsent(sample);

        boolean valid = sampleClassValidator.isValid(request);

        assertFalse(valid);
    }

    @Test
    public void whenRequestHasOneTumorClassAndTypeSampleWithSampleInfoNormalClassNormalType_shouldReturnInvalid() {
        Sample sample = getSample(TUMOR_CLASS, TumorNormalType.TUMOR, NORMAL_CLASS, TumorNormalType.NORMAL);
        request.putSampleIfAbsent(sample);

        boolean valid = sampleClassValidator.isValid(request);

        assertFalse(valid);
    }

    @Test
    public void whenRequestHasOneNormalClassAndTypeSampleNoSampleInfo_shouldReturnValid() {
        Sample sample = getSample(NORMAL_CLASS, TumorNormalType.NORMAL, "", null);
        request.putSampleIfAbsent(sample);

        boolean valid = sampleClassValidator.isValid(request);

        assertTrue(valid);
    }

    @Test
    public void whenRequestHasOneNormalClassAndTypeSampleWithSampleInfoNormalClassNormalType_shouldReturnValid() {
        Sample sample = getSample(NORMAL_CLASS, TumorNormalType.NORMAL, NORMAL_CLASS, TumorNormalType.NORMAL);
        request.putSampleIfAbsent(sample);

        boolean valid = sampleClassValidator.isValid(request);

        assertTrue(valid);
    }

    @Test
    public void whenRequestHasOneNormalClassAndTypeSampleWithSampleInfoNormalClassTumorType_shouldReturnInvalid() {
        Sample sample = getSample(NORMAL_CLASS, TumorNormalType.NORMAL, NORMAL_CLASS, TumorNormalType.TUMOR);
        request.putSampleIfAbsent(sample);

        boolean valid = sampleClassValidator.isValid(request);

        assertFalse(valid);
    }

    @Test
    public void whenRequestHasOneNormalClassAndTypeSampleWithSampleInfoTumorClassNormalType_shouldReturnInvalid() {
        Sample sample = getSample(NORMAL_CLASS, TumorNormalType.NORMAL, TUMOR_CLASS, TumorNormalType.NORMAL);
        request.putSampleIfAbsent(sample);

        boolean valid = sampleClassValidator.isValid(request);

        assertFalse(valid);
    }

    @Test
    public void whenRequestHasOneNormalClassAndTypeSampleWithSampleInfoTumorClassTumorType_shouldReturnInvalid() {
        Sample sample = getSample(NORMAL_CLASS, TumorNormalType.NORMAL, TUMOR_CLASS, TumorNormalType.TUMOR);
        request.putSampleIfAbsent(sample);

        boolean valid = sampleClassValidator.isValid(request);

        assertFalse(valid);
    }

    @Test
    public void whenRequestHasOneValidTumorAndOneValidNormalSample_shouldReturnValid() {
        Sample normalSample = getSample(NORMAL_CLASS, TumorNormalType.NORMAL, NORMAL_CLASS, TumorNormalType.NORMAL);
        Sample tumorSample = getSample(TUMOR_CLASS, TumorNormalType.TUMOR, TUMOR_CLASS, TumorNormalType.TUMOR);
        request.putSampleIfAbsent(normalSample);
        request.putSampleIfAbsent(tumorSample);

        boolean valid = sampleClassValidator.isValid(request);

        assertTrue(valid);
    }

    @Test
    public void whenRequestHasOneValidTumorAndOneInvalidSample_shouldReturnInvalid() {
        Sample normalSample = getSample(NORMAL_CLASS, TumorNormalType.NORMAL, NORMAL_CLASS, TumorNormalType.NORMAL);
        Sample invalid = getSample(TUMOR_CLASS, TumorNormalType.TUMOR, TUMOR_CLASS, TumorNormalType.NORMAL);
        request.putSampleIfAbsent(normalSample);
        request.putSampleIfAbsent(invalid);

        boolean valid = sampleClassValidator.isValid(request);

        assertFalse(valid);
    }

    @Test
    public void whenRequestHasMultipleValidSamples_shouldReturnValid() {
        Sample normalValidSample = getSample(NORMAL_CLASS, TumorNormalType.NORMAL, NORMAL_CLASS, TumorNormalType.NORMAL);
        Sample normalValidSample2 = getSample(NORMAL_CLASS, TumorNormalType.NORMAL, NORMAL_CLASS, TumorNormalType.NORMAL);
        Sample tumorValidSample = getSample(TUMOR_CLASS, TumorNormalType.TUMOR, TUMOR_CLASS, TumorNormalType.TUMOR);
        Sample tumorValidSample2 = getSample(TUMOR_CLASS, TumorNormalType.TUMOR, TUMOR_CLASS, TumorNormalType.TUMOR);
        request.putSampleIfAbsent(normalValidSample);
        request.putSampleIfAbsent(normalValidSample2);
        request.putSampleIfAbsent(tumorValidSample);
        request.putSampleIfAbsent(tumorValidSample2);

        boolean valid = sampleClassValidator.isValid(request);

        assertTrue(valid);
    }

    @Test
    public void whenRequestHasMultipleValidSamplesAndOneInvalidSample_shouldReturnInvalid() {
        Sample normalValidSample = getSample(NORMAL_CLASS, TumorNormalType.NORMAL, NORMAL_CLASS, TumorNormalType.NORMAL);
        Sample normalValidSample2 = getSample(NORMAL_CLASS, TumorNormalType.NORMAL, NORMAL_CLASS, TumorNormalType.NORMAL);
        Sample tumorValidSample = getSample(TUMOR_CLASS, TumorNormalType.TUMOR, TUMOR_CLASS, TumorNormalType.TUMOR);
        Sample tumorValidSample2 = getSample(TUMOR_CLASS, TumorNormalType.TUMOR, TUMOR_CLASS, TumorNormalType.TUMOR);
        Sample invalid = getSample(TUMOR_CLASS, TumorNormalType.TUMOR, TUMOR_CLASS, TumorNormalType.NORMAL);
        request.putSampleIfAbsent(normalValidSample);
        request.putSampleIfAbsent(normalValidSample2);
        request.putSampleIfAbsent(tumorValidSample);
        request.putSampleIfAbsent(tumorValidSample2);
        request.putSampleIfAbsent(invalid);

        boolean valid = sampleClassValidator.isValid(request);

        assertFalse(valid);
    }

    private Sample getSample(String sampleClass, TumorNormalType tumorNormalType, String sampleInfoClass, TumorNormalType sampleInfoTumorNormalType) {
        Sample sample = new Sample(getNextSampleId());
        sample.setSampleClass(sampleClass);
        sample.setTumorNormalType(tumorNormalType);
        sample.setCmoSampleInfo(getSampleInfo(sampleInfoClass, sampleInfoTumorNormalType));
        return sample;
    }

    private String getNextSampleId() {
        return "12345_P" + (sampleCounter++);
    }

    private CmoSampleInfo getSampleInfo(String type, TumorNormalType tumorNormalType) {
        CmoSampleInfo sampleInfo = new CmoSampleInfo();
        sampleInfo.setSampleClass(type);
        sampleInfo.setTumorNormalType(tumorNormalType);
        return sampleInfo;
    }

}