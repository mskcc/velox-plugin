package com.velox.sloan.workflows.validator.converter;

import org.hamcrest.object.IsCompatibleType;
import org.junit.Test;
import org.mskcc.domain.sample.CmoSampleInfo;
import org.mskcc.domain.sample.TumorNormalType;
import org.mskcc.util.TestUtils;
import org.mskcc.util.VeloxConstants;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class CmoSampleInfoConverterTest {
    private CmoSampleInfoConverter cmoSampleInfoConverter = new CmoSampleInfoConverter();

    @Test
    public void whenSampleInfoContainsNormalSampleClassAndNormalType_shouldCmoSampleInfoContainNormalClassAndNormalType() {
        Map<String, Object> sampleInfo = new HashMap<>();
        String normalSampleClass = "Normal";
        sampleInfo.put(VeloxConstants.CMO_SAMPLE_CLASS, normalSampleClass);
        sampleInfo.put(VeloxConstants.TUMOR_OR_NORMAL, "Normal");

        CmoSampleInfo cmoSampleInfo = cmoSampleInfoConverter.convert(sampleInfo);

        assertThat(cmoSampleInfo.getCMOSampleClass(), is(normalSampleClass));
        assertThat(cmoSampleInfo.getTumorOrNormal(), is(TumorNormalType.NORMAL.getValue()));
    }

    @Test
    public void whenSampleInfoContainsNormalSampleClassAndTumorType_shouldCmoSampleInfoContainNormalClassAndTumorType() {
        Map<String, Object> sampleInfo = new HashMap<>();
        String normalSampleClass = "Normal";
        sampleInfo.put(VeloxConstants.CMO_SAMPLE_CLASS, normalSampleClass);
        sampleInfo.put(VeloxConstants.TUMOR_OR_NORMAL, "Tumor");

        CmoSampleInfo cmoSampleInfo = cmoSampleInfoConverter.convert(sampleInfo);

        assertThat(cmoSampleInfo.getCMOSampleClass(), is(normalSampleClass));
        assertThat(cmoSampleInfo.getTumorOrNormal(), is(TumorNormalType.TUMOR.getValue()));
    }

    @Test
    public void whenSampleInfoContainsTumorSampleClassAndTumorType_shouldCmoSampleInfoContainTumorClassAndTumorType() {
        Map<String, Object> sampleInfo = new HashMap<>();
        String sampleClas = "Tumor";
        sampleInfo.put(VeloxConstants.CMO_SAMPLE_CLASS, sampleClas);
        sampleInfo.put(VeloxConstants.TUMOR_OR_NORMAL, "Tumor");

        CmoSampleInfo cmoSampleInfo = cmoSampleInfoConverter.convert(sampleInfo);

        assertThat(cmoSampleInfo.getCMOSampleClass(), is(sampleClas));
        assertThat(cmoSampleInfo.getTumorOrNormal(), is(TumorNormalType.TUMOR.getValue()));
    }

    @Test
    public void whenSampleInfoContainsTumorSampleClassAndNormalType_shouldCmoSampleInfoContainTumorClassAndNormalType() {
        Map<String, Object> sampleInfo = new HashMap<>();
        String sampleClass = "Tumor";
        sampleInfo.put(VeloxConstants.CMO_SAMPLE_CLASS, sampleClass);
        sampleInfo.put(VeloxConstants.TUMOR_OR_NORMAL, "Normal");

        CmoSampleInfo cmoSampleInfo = cmoSampleInfoConverter.convert(sampleInfo);

        assertThat(cmoSampleInfo.getCMOSampleClass(), is(sampleClass));
        assertThat(cmoSampleInfo.getTumorOrNormal(), is(TumorNormalType.NORMAL.getValue()));
    }

    @Test
    public void whenSampleInfoContainsUnsupportedSampleType_shouldThrowAnException() {
        Map<String, Object> sampleInfo = new HashMap<>();
        String sampleClass = "Tumor";
        sampleInfo.put(VeloxConstants.CMO_SAMPLE_CLASS, sampleClass);
        sampleInfo.put(VeloxConstants.TUMOR_OR_NORMAL, "notExisting");

        Optional<Exception> exception = TestUtils.assertThrown(() -> cmoSampleInfoConverter.convert(sampleInfo));

        assertThat(exception.isPresent(), is(true));
        assertThat(exception.get().getClass(), IsCompatibleType.typeCompatibleWith(TumorNormalType.UnsupportedTumorNormalTypeException.class));
    }

}