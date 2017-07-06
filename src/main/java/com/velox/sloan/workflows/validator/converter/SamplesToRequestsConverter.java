package com.velox.sloan.workflows.validator.converter;

import org.mskcc.domain.Request;
import org.mskcc.domain.Sample;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SamplesToRequestsConverter {
    public Map<String, Request> convert(List<Sample> samples) {
        if (samples == null)
            return Collections.emptyMap();

        Map<String, Request> requests = new HashMap<>();
        for (Sample sample : samples) {
            String reqId = sample.getRequestId();
            requests.putIfAbsent(reqId, new Request(reqId));
            requests.get(reqId).putSampleIfAbsent(sample);
        }
        return requests;
    }
}
