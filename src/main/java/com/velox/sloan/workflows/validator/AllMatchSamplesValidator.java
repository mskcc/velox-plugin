package com.velox.sloan.workflows.validator;

import org.mskcc.domain.Request;
import org.mskcc.domain.sample.Sample;

import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class AllMatchSamplesValidator implements SamplesValidator {
    @Override
    public boolean isValid(Request request, Predicate<Sample> predicate) {
        Set<Sample> nonValidSamples = getNonValidSamples(request, predicate);
        return nonValidSamples.size() == 0;
    }

    @Override
    public Set<Sample> getNonValidSamples(Request request, Predicate<Sample> predicate) {
        return request.getSamples().values().stream()
                .filter(s -> !predicate.test(s))
                .collect(Collectors.toSet());
    }
}
