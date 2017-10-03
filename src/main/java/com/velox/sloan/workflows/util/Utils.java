package com.velox.sloan.workflows.util;

import org.mskcc.domain.sample.Sample;

import java.util.Set;
import java.util.stream.Collectors;

public class Utils {
    public static String getJoinedIgoIds(Set<Sample> nonValidSamples) {
        return nonValidSamples.stream()
                .map(s -> s.getIgoId())
                .collect(Collectors.joining(", "));
    }

    public static String getFormattedValue(Object value) {
        if (value == null || value == "")
            return "\"\"";
        return String.format("\"%s\"", value);
    }
}
