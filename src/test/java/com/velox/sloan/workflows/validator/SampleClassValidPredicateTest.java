package com.velox.sloan.workflows.validator;

import org.junit.Test;
import org.mskcc.domain.sample.CmoSampleInfo;
import org.mskcc.domain.sample.Sample;
import org.mskcc.domain.sample.TumorNormalType;

import static com.velox.sloan.workflows.validator.SampleClassValidatorTest.NORMAL_CLASS;
import static com.velox.sloan.workflows.validator.SampleClassValidatorTest.TUMOR_CLASS;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SampleClassValidPredicateTest {
    private final SampleClassValidPredicate sampleClassValidPredicate = new SampleClassValidPredicate();
    private int sampleCounter = 0;

    @Test
    public void whenRequestHasOneTumorClassAndTypeSampleWithNoSampleInfo_shouldReturnValid() {
        Sample sample = getSample(TUMOR_CLASS, TumorNormalType.TUMOR, "", null);

        boolean valid = sampleClassValidPredicate.test(sample);

        assertTrue(valid);
    }

    @Test
    public void whenRequestHasOneTumorClassAndTypeSampleWithTumorClassAndTypeSampleInfo_shouldReturnValid() {
        Sample sample = getSample(TUMOR_CLASS, TumorNormalType.TUMOR, TUMOR_CLASS, TumorNormalType.TUMOR);

        boolean valid = sampleClassValidPredicate.test(sample);

        assertTrue(valid);
    }

    @Test
    public void whenRequestHasOneTumorClassAndTypeSampleWithSampleTumorClassNormalTypeInfo_shouldReturnInvalid() {
        Sample sample = getSample(TUMOR_CLASS, TumorNormalType.TUMOR, TUMOR_CLASS, TumorNormalType.NORMAL);

        boolean valid = sampleClassValidPredicate.test(sample);

        assertFalse(valid);
    }

    @Test
    public void whenRequestHasOneTumorClassAndTypeSampleWithSampleNormalClassTumorTypeInfo_shouldReturnInvalid() {
        Sample sample = getSample(TUMOR_CLASS, TumorNormalType.TUMOR, NORMAL_CLASS, TumorNormalType.TUMOR);

        boolean valid = sampleClassValidPredicate.test(sample);

        assertFalse(valid);
    }

    @Test
    public void whenRequestHasOneTumorClassAndTypeSampleWithSampleInfoNormalClassNormalType_shouldReturnInvalid() {
        Sample sample = getSample(TUMOR_CLASS, TumorNormalType.TUMOR, NORMAL_CLASS, TumorNormalType.NORMAL);

        boolean valid = sampleClassValidPredicate.test(sample);

        assertFalse(valid);
    }

    @Test
    public void whenRequestHasOneNormalClassAndTypeSampleNoSampleInfo_shouldReturnValid() {
        Sample sample = getSample(NORMAL_CLASS, TumorNormalType.NORMAL, "", null);

        boolean valid = sampleClassValidPredicate.test(sample);

        assertTrue(valid);
    }

    @Test
    public void whenRequestHasOneNormalClassAndTypeSampleWithSampleInfoNormalClassNormalType_shouldReturnValid() {
        Sample sample = getSample(NORMAL_CLASS, TumorNormalType.NORMAL, NORMAL_CLASS, TumorNormalType.NORMAL);

        boolean valid = sampleClassValidPredicate.test(sample);

        assertTrue(valid);
    }

    @Test
    public void whenRequestHasOneNormalClassAndTypeSampleWithSampleInfoNormalClassTumorType_shouldReturnInvalid() {
        Sample sample = getSample(NORMAL_CLASS, TumorNormalType.NORMAL, NORMAL_CLASS, TumorNormalType.TUMOR);

        boolean valid = sampleClassValidPredicate.test(sample);

        assertFalse(valid);
    }

    @Test
    public void whenRequestHasOneNormalClassAndTypeSampleWithSampleInfoTumorClassNormalType_shouldReturnInvalid() {
        Sample sample = getSample(NORMAL_CLASS, TumorNormalType.NORMAL, TUMOR_CLASS, TumorNormalType.NORMAL);

        boolean valid = sampleClassValidPredicate.test(sample);

        assertFalse(valid);
    }

    @Test
    public void whenRequestHasOneNormalClassAndTypeSampleWithSampleInfoTumorClassTumorType_shouldReturnInvalid() {
        Sample sample = getSample(NORMAL_CLASS, TumorNormalType.NORMAL, TUMOR_CLASS, TumorNormalType.TUMOR);

        boolean valid = sampleClassValidPredicate.test(sample);

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
        sampleInfo.setCMOSampleClass(type);
        sampleInfo.setTumorOrNormal(tumorNormalType == null ? "" : tumorNormalType.getValue());
        return sampleInfo;
    }
}