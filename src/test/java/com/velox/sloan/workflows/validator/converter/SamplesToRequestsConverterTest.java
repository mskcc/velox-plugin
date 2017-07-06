package com.velox.sloan.workflows.validator.converter;

import org.junit.Test;
import org.mskcc.domain.Recipe;
import org.mskcc.domain.Request;
import org.mskcc.domain.Sample;

import java.util.*;
import java.util.function.Predicate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class SamplesToRequestsConverterTest {
    public static final String REQUEST_ID_1 = "12345_A";
    public static final String REQUEST_ID_2 = "12345_B";
    public static final String REQUEST_ID_3 = "12345_C";
    private Map<String, Request> initialRequests = getInitialRequests();

    private Map<String, Request> getInitialRequests() {
        Map<String, Request> requests = new HashMap<>();
        requests.put(REQUEST_ID_1, new Request(REQUEST_ID_1));
        requests.put(REQUEST_ID_2, new Request(REQUEST_ID_2));
        requests.put(REQUEST_ID_3, new Request(REQUEST_ID_3));

        return requests;
    }

    @Test
    public void whenSampleListContainsOneSample_shouldReturnOneElementRequestListWithOneSample() {
        SamplesToRequestsConverter samplesToRequestsConverter = new SamplesToRequestsConverter();

        String igoId = "12345_A_1";
        String reqId = REQUEST_ID_1;
        Recipe recipe = Recipe.AMPLI_SEQ;

        Sample sample = getSample(igoId, reqId, recipe);

        List<Sample> samples = Arrays.asList(sample);
        Map<String, Request> requests = samplesToRequestsConverter.convert(samples);

        assertRequestContainsSamples(requests.values(), samples);
    }

    @Test
    public void whenSampleListContainsTwoSamplesFromSameRequest_shouldReturnOneElementRequestListWithTwoSamples() {
        SamplesToRequestsConverter samplesToRequestsConverter = new SamplesToRequestsConverter();

        String igoId1 = "12345_A_1";
        String igoId2 = "12345_A_2";
        String reqId = REQUEST_ID_1;
        Recipe recipe = Recipe.AMPLI_SEQ;

        Sample sample1 = getSample(igoId1, reqId, recipe);
        Sample sample2 = getSample(igoId2, reqId, recipe);

        List<Sample> samples = Arrays.asList(sample1, sample2);
        Map<String, Request> requests = samplesToRequestsConverter.convert(samples);

        assertRequestContainsSamples(requests.values(), samples);
    }

    @Test
    public void whenSampleListContainsTwoSamplesFromTwoRequests_shouldReturnTwoRequestsWithOneSampleEach() {
        SamplesToRequestsConverter samplesToRequestsConverter = new SamplesToRequestsConverter();
        Recipe recipe = Recipe.AMPLI_SEQ;

        Sample sample1 = getSample("12345_A_1", REQUEST_ID_1, recipe);
        Sample sample2 = getSample("12345_B_1", REQUEST_ID_2, recipe);
        List<Sample> samples = Arrays.asList(sample1, sample2);

        Map<String, Request> requests = samplesToRequestsConverter.convert(samples);

        assertRequestContainsSamples(requests.values(), Arrays.asList(sample1));
        assertRequestContainsSamples(requests.values(), Arrays.asList(sample2));
    }

    @Test
    public void whenSampleListContainsMultipleSamplesFromMultipleRequests_shouldReturnRequestsWithSamples() {
        SamplesToRequestsConverter samplesToRequestsConverter = new SamplesToRequestsConverter();
        Recipe recipe = Recipe.AMPLI_SEQ;

        Sample sample1 = getSample("12345_A_1", REQUEST_ID_1, recipe);
        Sample sample2 = getSample("12345_A_2", REQUEST_ID_1, recipe);
        Sample sample3 = getSample("12345_B_1", REQUEST_ID_2, recipe);
        Sample sample4 = getSample("12345_B_2", REQUEST_ID_2, recipe);
        Sample sample5 = getSample("12345_B_3", REQUEST_ID_2, recipe);
        Sample sample6 = getSample("12345_B_4", REQUEST_ID_2, recipe);
        Sample sample7 = getSample("12345_B_5", REQUEST_ID_2, recipe);
        Sample sample8 = getSample("12345_C_1", REQUEST_ID_3, recipe);
        List<Sample> samples = Arrays.asList(sample1, sample2, sample3, sample4, sample5, sample6, sample7, sample8);

        Map<String, Request> requests = samplesToRequestsConverter.convert(samples);

        assertRequestContainsSamples(requests.values(), Arrays.asList(sample1, sample2));
        assertRequestContainsSamples(requests.values(), Arrays.asList(sample3, sample4, sample5, sample6, sample7));
        assertRequestContainsSamples(requests.values(), Arrays.asList(sample8));
    }

    private void assertRequestContainsSamples(Collection<Request> requests, List<Sample> samples) {
        for (Sample sample : samples) {
            Predicate<Request> requestMatchPredicate = new RequestMatchPredicate(sample.getRequestId(), sample.getIgoId(), sample.getRecipe());
            assertThat(requests.stream().filter(requestMatchPredicate).count(), is(1l));
        }
    }

    private Sample getSample(String igoId, String reqId, Recipe recipe) {
        Sample sample = new Sample(igoId);
        sample.setRequestId(reqId);
        sample.setRecipe(Recipe.getRecipeByValue(recipe.getValue()));
        return sample;
    }

    class RequestMatchPredicate implements Predicate<Request> {
        private String reqId;
        private String igoId;
        private Recipe recipe;

        RequestMatchPredicate(String reqId, String igoId, Recipe recipe) {
            this.reqId = reqId;
            this.igoId = igoId;
            this.recipe = recipe;
        }

        @Override
        public boolean test(Request request) {
            return request.getId().equals(reqId)
                    && request.getSamples().containsKey(igoId)
                    && request.getSample(igoId).getRecipe() == recipe
                    && request.getSample(igoId).getRequestId() == reqId;
        }
    }

}