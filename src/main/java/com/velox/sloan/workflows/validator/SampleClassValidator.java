package com.velox.sloan.workflows.validator;

import com.velox.sloan.workflows.notificator.BulkNotificator;
import com.velox.sloan.workflows.util.Utils;
import org.mskcc.domain.Request;
import org.mskcc.domain.sample.Sample;

import java.util.function.Predicate;
import java.util.stream.Collectors;

public class SampleClassValidator implements Validator {
    private final BulkNotificator notificator;
    private final Predicate<Sample> sampleClassValidPredicate;

    public SampleClassValidator(BulkNotificator notificator, Predicate<Sample> sampleClassValidPredicate) {
        this.notificator = notificator;
        this.sampleClassValidPredicate = sampleClassValidPredicate;
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

    @Override
    public String getMessage(Request request) {
        String samplesWithAmbiguousClass = request.getSamples().values().stream()
                .filter(s -> !sampleClassValidPredicate.test(s))
                .map(s -> getSampleClassDescription(s))
                .collect(Collectors.joining(",\n"));

        return String.format("Request %s has samples with ambiguous sample class/type: \n%s", request.getId(), samplesWithAmbiguousClass);
    }

    private String getSampleClassDescription(Sample s) {
        return String.format("\tsample: %s [sample class: %s, tumor/normal: %s, cmo info sample class: %s, cmo info tumor/normal: %s]",
                s.getIgoId(),
                Utils.getFormattedValue(s.getSampleClass()),
                Utils.getFormattedValue(s.getTumorNormalType()),
                Utils.getFormattedValue(s.getCmoSampleInfo().getSampleClass()),
                Utils.getFormattedValue(s.getCmoSampleInfo().getTumorNormalType()));
    }

    @Override
    public String getName() {
        return "Sample class validator";
    }

    @Override
    public boolean shouldValidate(Request request) {
        return true;
    }

}
