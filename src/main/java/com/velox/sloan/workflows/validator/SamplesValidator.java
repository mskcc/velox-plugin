package com.velox.sloan.workflows.validator;

import org.mskcc.domain.Request;
import org.mskcc.domain.sample.Sample;

import java.util.Set;
import java.util.function.Predicate;

public interface SamplesValidator {
    boolean isValid(Request request, Predicate<Sample> predicate);

    Set<Sample> getNonValidSamples(Request request, Predicate<Sample> predicate);
}
