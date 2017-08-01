package com.velox.sloan.workflows.validator;

import org.mskcc.domain.Request;
import org.mskcc.domain.RequestSpecies;
import org.mskcc.domain.XenograftClass;
import org.mskcc.domain.sample.Sample;
import org.mskcc.util.Constants;

public class TestUtils {
    private static int sampleId = 1;

    static Sample getChickenSample() {
        Sample sample = new Sample(getIgoId());
        sample.put(Constants.SPECIES, RequestSpecies.BACTERIA.getValue());
        return sample;
    }

    static Sample getBacteriaSample() {
        Sample sample = new Sample(getIgoId());
        sample.put(Constants.SPECIES, RequestSpecies.CHICKEN.getValue());
        return sample;
    }

    static Sample getHumanSample() {
        Sample sample = new Sample(getIgoId());
        sample.put(Constants.SPECIES, RequestSpecies.HUMAN.getValue());
        return sample;
    }

    static Sample getNonXenograftSample() {
        Sample sample = new Sample(getIgoId());
        sample.put(Constants.SAMPLE_TYPE, "nonXenograftSampleType");
        return sample;
    }

    static Sample getXenograftSample() {
        Sample sample = new Sample(getIgoId());
        sample.put(Constants.SAMPLE_TYPE, XenograftClass.PDX.getValue());
        sample.put(Constants.SPECIES, RequestSpecies.XENOGRAFT.getValue());
        return sample;
    }

    private static String getIgoId() {
        return "12345_P" + (sampleId++);
    }

    public static Request getRequestWithName(String name) {
        Request request = new Request("12345_P");
        request.setName(name);

        return request;
    }


}
