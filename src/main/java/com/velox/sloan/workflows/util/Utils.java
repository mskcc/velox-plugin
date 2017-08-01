package com.velox.sloan.workflows.util;

import org.mskcc.domain.sample.Sample;

import java.util.Set;
import java.util.stream.Collectors;

public class Utils {
    public static String getJoinedIgoAndCmoSamplesIds(Set<Sample> nonValidSamples) {
        return nonValidSamples.stream()
                .map(s -> String.format("%s (%s)", s.getIgoId(), s.getCmoSampleId()))
                .collect(Collectors.joining(","));
    }
}
