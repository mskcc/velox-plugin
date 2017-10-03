package com.velox.sloan.workflows.validator;

import org.apache.commons.lang3.StringUtils;
import org.mskcc.domain.sample.Sample;
import org.mskcc.domain.sample.TumorNormalType;
import org.mskcc.util.Constants;

import java.util.function.Predicate;

public class SampleClassValidPredicate implements Predicate<Sample> {
    @Override
    public boolean test(Sample sample) {
        String sampleClass = sample.getSampleClass();
        TumorNormalType tumorNormalType = sample.getTumorNormalType();

        String cmoInfoSampleClass = sample.getCmoSampleInfo().getSampleClass();
        TumorNormalType cmoInfoTumorNormalType = sample.getCmoSampleInfo().getTumorNormalType();

        if (isSampleClassNormal(sampleClass))
            return isNormalType(tumorNormalType)
                    && (StringUtils.isEmpty(cmoInfoSampleClass) || isSampleClassNormal(cmoInfoSampleClass))
                    && (cmoInfoTumorNormalType == null || isNormalType(cmoInfoTumorNormalType));

        return isTumorType(tumorNormalType)
                && (StringUtils.isEmpty(cmoInfoSampleClass) || isSampleClassTumor(cmoInfoSampleClass))
                && (cmoInfoTumorNormalType == null || isTumorType(cmoInfoTumorNormalType));
    }

    private boolean isSampleClassTumor(String sampleClass) {
        return !sampleClass.contains(Constants.NORMAL);
    }

    private boolean isTumorType(TumorNormalType tumorNormalType) {
        return tumorNormalType == TumorNormalType.TUMOR;
    }

    private boolean isNormalType(TumorNormalType tumorNormalType) {
        return tumorNormalType == TumorNormalType.NORMAL;
    }

    private boolean isSampleClassNormal(String sampleClass) {
        return sampleClass.contains(Constants.NORMAL);
    }
}
