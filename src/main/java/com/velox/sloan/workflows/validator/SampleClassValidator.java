package com.velox.sloan.workflows.validator;

import com.velox.sloan.workflows.notificator.BulkNotificator;
import org.apache.commons.lang3.StringUtils;
import org.mskcc.domain.Request;
import org.mskcc.domain.sample.Sample;
import org.mskcc.domain.sample.TumorNormalType;
import org.mskcc.util.Constants;

import java.util.function.Predicate;
import java.util.stream.Collectors;

public class SampleClassValidator implements Validator {
    private final Predicate<Sample> sampleClassValidPredicate = new SampleClassValidPredicate();
    private BulkNotificator notificator;

    public SampleClassValidator(BulkNotificator notificator) {
        this.notificator = notificator;
    }

    @Override
    public boolean isValid(Request request) {
        return request.getSamples().values().stream()
                .allMatch(s -> sampleClassValidPredicate.test(s));
    }

    @Override
    public BulkNotificator getBulkNotificator() {
        return notificator;
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

    @Override
    public String getMessage(Request request) {
        String samplesWithAmbiguousClass = request.getSamples().values().stream().filter(s -> !sampleClassValidPredicate.test(s)).map(s -> s.getIgoId()).collect(Collectors.joining(","));
        return String.format("Request %s has samples with ambiguous sample class: %s", request.getId(), samplesWithAmbiguousClass);
    }

    @Override
    public String getName() {
        return "Sample class validator";
    }

    @Override
    public boolean shouldValidate(Request request) {
        return true;
    }

    private class SampleClassValidPredicate implements Predicate<Sample> {
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
    }
}
