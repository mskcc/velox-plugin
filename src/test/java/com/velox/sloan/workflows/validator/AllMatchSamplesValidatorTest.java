package com.velox.sloan.workflows.validator;

import org.junit.Test;
import org.mskcc.domain.Request;
import org.mskcc.domain.sample.Sample;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class AllMatchSamplesValidatorTest {
    private AllMatchSamplesValidator samplesValidator = new AllMatchSamplesValidator();
    private Request request = new Request("123_P");
    private int id;
    private Predicate<Sample> predicateMock = new PredicateMock();

    @Test
    public void whenRequestHasNoSamples_shouldBeValid() {
        boolean valid = samplesValidator.isValid(request, predicateMock);
        Set<Sample> nonValidSamples = samplesValidator.getNonValidSamples(request, predicateMock);

        assertThat(valid, is(true));
        assertThat(nonValidSamples.size(), is(0));
    }

    @Test
    public void whenRequestHasOneValidSample_shouldBeValid() {
        request.putSampleIfAbsent(getValidSample());

        boolean valid = samplesValidator.isValid(request, predicateMock);
        Set<Sample> nonValidSamples = samplesValidator.getNonValidSamples(request, predicateMock);

        assertThat(valid, is(true));
        assertThat(nonValidSamples.size(), is(0));
    }

    @Test
    public void whenRequestHasOneInvalidSample_shouldBeInvalid() {
        Sample invalidSample = getInvalidSample();
        request.putSampleIfAbsent(invalidSample);

        boolean valid = samplesValidator.isValid(request, predicateMock);
        Set<Sample> nonValidSamples = samplesValidator.getNonValidSamples(request, predicateMock);

        assertThat(valid, is(false));
        assertThat(nonValidSamples.size(), is(1));
        assertThat(nonValidSamples.contains(invalidSample), is(true));
    }

    @Test
    public void whenRequestHasMultipleValidSamples_shouldBeValid() {
        request.putSampleIfAbsent(getValidSample());
        request.putSampleIfAbsent(getValidSample());
        request.putSampleIfAbsent(getValidSample());
        request.putSampleIfAbsent(getValidSample());

        boolean valid = samplesValidator.isValid(request, predicateMock);
        Set<Sample> nonValidSamples = samplesValidator.getNonValidSamples(request, predicateMock);

        assertThat(valid, is(true));
        assertThat(nonValidSamples.size(), is(0));
    }

    @Test
    public void whenRequestHasMultipleInvalidSamples_shouldBeInvalid() {
        int numberOfInvalidSamples = 6;
        List<Sample> samples = getInvalidSamples(numberOfInvalidSamples);
        for (Sample sample : samples) {
            request.putSampleIfAbsent(sample);
        }

        boolean valid = samplesValidator.isValid(request, predicateMock);
        Set<Sample> nonValidSamples = samplesValidator.getNonValidSamples(request, predicateMock);

        assertThat(valid, is(false));
        assertThat(nonValidSamples.size(), is(numberOfInvalidSamples));
        assertThat(nonValidSamples.containsAll(samples), is(true));
    }

    @Test
    public void whenRequestHasMultipleValidSamplesAndOneInvalid_shouldBeInvalid() {
        request.putSampleIfAbsent(getValidSample());
        request.putSampleIfAbsent(getValidSample());
        request.putSampleIfAbsent(getValidSample());
        Sample invalidSample = getInvalidSample();
        request.putSampleIfAbsent(invalidSample);

        boolean valid = samplesValidator.isValid(request, predicateMock);
        Set<Sample> nonValidSamples = samplesValidator.getNonValidSamples(request, predicateMock);

        assertThat(valid, is(false));
        assertThat(nonValidSamples.size(), is(1));
        assertThat(nonValidSamples.contains(invalidSample), is(true));
    }

    private Sample getInvalidSample() {
        return new Sample(String.format("invalid%d", id++));
    }

    private Sample getValidSample() {
        return new Sample(String.format("valid_%d", id++));
    }

    private List<Sample> getInvalidSamples(int numberOfSamples) {
        List<Sample> samples = new ArrayList<>();

        for (int i = 0; i < numberOfSamples; i++) {
            samples.add(getInvalidSample());
        }

        return samples;
    }

    private class PredicateMock implements Predicate<Sample> {
        @Override
        public boolean test(Sample sample) {
            return sample.getIgoId().startsWith("valid");
        }
    }
}