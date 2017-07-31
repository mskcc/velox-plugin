package com.velox.sloan.workflows.validator.converter;

import com.velox.api.datarecord.DataRecord;
import com.velox.sloan.workflows.validator.retriever.VeloxRequestRetriever;
import org.junit.Before;
import org.junit.Test;
import org.mskcc.domain.*;
import org.mskcc.domain.sample.Sample;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SampleRecordsToRequestsConverterTest {
    public static final String REQUEST_ID_1 = "12345_A";
    public static final String REQUEST_ID_2 = "12345_B";
    public static final String REQUEST_ID_3 = "12345_C";
    private Map<String, Request> initialRequests = getInitialRequests();
    private Converter<DataRecord, Sample> sampleConverter = mock(Converter.class);
    private SampleRecordsToRequestsConverter sampleRecordsToRequestsConverter;
    private VeloxRequestRetriever requestRetriever;
    private RequestConverter requestConverter;

    @Before
    public void setUp() throws Exception {
        requestConverter = mock(RequestConverter.class);
        requestRetriever = mock(VeloxRequestRetriever.class);
        sampleRecordsToRequestsConverter = new SampleRecordsToRequestsConverter(sampleConverter,
                requestRetriever, requestConverter, r -> true);
    }

    private Map<String, Request> getInitialRequests() {
        Map<String, Request> requests = new HashMap<>();
        requests.put(REQUEST_ID_1, new Request(REQUEST_ID_1));
        requests.put(REQUEST_ID_2, new Request(REQUEST_ID_2));
        requests.put(REQUEST_ID_3, new Request(REQUEST_ID_3));

        return requests;
    }

    @Test
    public void whenSampleListContainsOneSample_shouldReturnOneElementRequestListWithOneSample() throws Exception {
        Sample sample = getSample("12345_A_1", REQUEST_ID_1, Recipe.AMPLI_SEQ);

        Map<String, Request> requests = sampleRecordsToRequestsConverter.convert(getSampleRecords(Arrays.asList(sample)));

        assertRequestContainsSamples(requests, Arrays.asList(sample));
    }

    @Test
    public void whenSampleListContainsTwoSamplesFromSameRequest_shouldReturnOneElementRequestListWithTwoSamples() throws Exception {
        String igoId1 = "12345_A_1";
        String igoId2 = "12345_A_2";
        String reqId = REQUEST_ID_1;
        Recipe recipe = Recipe.AMPLI_SEQ;

        Sample sample1 = getSample(igoId1, reqId, recipe);
        Sample sample2 = getSample(igoId2, reqId, recipe);

        List<Sample> samples = Arrays.asList(sample1, sample2);
        Map<String, Request> requests = sampleRecordsToRequestsConverter.convert(getSampleRecords(samples));

        assertRequestContainsSamples(requests, samples);
    }

    @Test
    public void whenSampleListContainsTwoSamplesFromTwoRequests_shouldReturnTwoRequestsWithOneSampleEach() throws Exception {
        Recipe recipe = Recipe.AMPLI_SEQ;

        Sample sample1 = getSample("12345_A_1", REQUEST_ID_1, recipe);
        Sample sample2 = getSample("12345_B_1", REQUEST_ID_2, recipe);
        List<Sample> samples = Arrays.asList(sample1, sample2);

        Map<String, Request> requests = sampleRecordsToRequestsConverter.convert(getSampleRecords(samples));

        assertRequestContainsSamples(requests, Arrays.asList(sample1));
        assertRequestContainsSamples(requests, Arrays.asList(sample2));
    }

    @Test
    public void whenSampleListContainsMultipleSamplesFromMultipleRequests_shouldReturnRequestsWithSamples() throws Exception {
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

        Map<String, Request> requests = sampleRecordsToRequestsConverter.convert(getSampleRecords(samples));

        assertRequestContainsSamples(requests, Arrays.asList(sample1, sample2));
        assertRequestContainsSamples(requests, Arrays.asList(sample3, sample4, sample5, sample6, sample7));
        assertRequestContainsSamples(requests, Arrays.asList(sample8));
    }

    @Test
    public void whenRequestHasOneSampleWithOneStrand_shouldRequestShouldContainThisOneStrand() throws Exception {
        List<Strand> strands = Arrays.asList(Strand.NONE);
        Sample sample = getSample("12345_A_1", REQUEST_ID_1, Recipe.AMPLI_SEQ, strands);

        Map<String, Request> requests = sampleRecordsToRequestsConverter.convert(getSampleRecords(Arrays.asList(sample)));

        assertRequestContainsSamples(requests, Arrays.asList(sample));
    }

    @Test
    public void whenRequestHasOneSampleWithMultipleStrand_shouldRequestShouldContainSampleMultipleStrands() throws Exception {
        Sample sample = getSample("12345_A_1", REQUEST_ID_1, Recipe.AMPLI_SEQ, Arrays.asList(Strand.NONE, Strand.REVERSE, Strand.REVERSE));

        Map<String, Request> requests = sampleRecordsToRequestsConverter.convert(getSampleRecords(Arrays.asList(sample)));

        assertRequestContainsSamples(requests, Arrays.asList(sample));
    }

    @Test
    public void whenRequestHasTwoSamplesWithDifferentStrands_shouldRequestContainAllSamplesStrands() throws Exception {
        Recipe recipe = Recipe.AMPLI_SEQ;

        Sample sample1 = getSample("12345_A_1", REQUEST_ID_1, recipe, Arrays.asList(Strand.REVERSE));
        Sample sample2 = getSample("12345_B_1", REQUEST_ID_1, recipe, Arrays.asList(Strand.NONE, Strand.REVERSE));
        List<Sample> samples = Arrays.asList(sample1, sample2);

        Map<String, Request> requests = sampleRecordsToRequestsConverter.convert(getSampleRecords(samples));

        assertRequestContainsSamples(requests, samples);
    }

    @Test
    public void whenRnaSeqPredicateIsTrue_shouldRequestHaveRnaSeqRequestTypeSet() throws Exception {
        Recipe recipe = Recipe.AMPLI_SEQ;

        Sample sample1 = getSample("12345_A_1", REQUEST_ID_1, recipe, Arrays.asList(Strand.REVERSE));
        List<Sample> samples = Arrays.asList(sample1);

        Map<String, Request> requests = sampleRecordsToRequestsConverter.convert(getSampleRecords(samples));
        assertThat(requests.get(REQUEST_ID_1).getRequestType(), is(RequestType.RNASEQ));
    }

    @Test
    public void whenRnaSeqpredicateIsFalse_shouldRequestHaveNoRnaSeqRequestTypeSet() throws Exception {
        Recipe recipe = Recipe.AMPLI_SEQ;

        Sample sample1 = getSample("12345_A_1", REQUEST_ID_1, recipe, Arrays.asList(Strand.REVERSE));
        List<Sample> samples = Arrays.asList(sample1);

        SampleRecordsToRequestsConverter samplesConverter = new SampleRecordsToRequestsConverter(sampleConverter, requestRetriever, requestConverter, r -> false);

        Map<String, Request> requests = samplesConverter.convert(getSampleRecords(samples));
        assertThat(requests.get(REQUEST_ID_1).getRequestType(), is(not(RequestType.RNASEQ)));
    }

    private void assertRequestContainsSamples(Map<String, Request> requests, List<Sample> samples) {
        assertAllSamplesFromOneRequest(samples);
        assertRequestExist(requests, samples);

        Request request = requests.get(samples.get(0).getRequestId());
        assertRequestContainsAllSamples(request, samples);
        assertRequestContainsAllSamplesStrands(samples, request);
    }

    private void assertRequestContainsAllSamplesStrands(List<Sample> samples, Request request) {
        Collection<Strand> allSamplesStrands = getAllSamplesStrands(samples);

        assertThat(request.getStrands().size(), is(allSamplesStrands.size()));
        assertThat(request.getStrands().containsAll(allSamplesStrands), is(true));
    }

    private Collection<Strand> getAllSamplesStrands(List<Sample> samples) {
        return samples.stream()
                .flatMap(s -> s.getStrands().stream())
                .collect(Collectors.toSet());
    }

    private void assertRequestContainsAllSamples(Request request, List<Sample> samples) {
        assertThat(request.getSamples().size(), is(samples.size()));
        BiPredicate<Sample, Sample> sameSamplePredicate = new SampleMatchPredicate();

        request.getSamples().values().stream()
                .allMatch(reqSample -> samples.stream()
                        .filter(s -> sameSamplePredicate.test(reqSample, s))
                        .count() == 1);
    }

    private void assertRequestExist(Map<String, Request> requests, List<Sample> samples) {
        assertThat(requests.containsKey(samples.get(0).getRequestId()), is(true));
    }

    private void assertAllSamplesFromOneRequest(List<Sample> samples) {
        assertThat(samples.stream().map(s -> s.getRequestId()).distinct().count(), is(1l));
    }

    private List<DataRecord> getSampleRecords(List<Sample> samples) throws Exception {
        List<DataRecord> sampleRecordMocks = new ArrayList<>();
        for (Sample sample : samples) {
            DataRecord sampleRecordMock = mock(DataRecord.class);
            when(sampleConverter.convert(sampleRecordMock)).thenReturn(sample);

            DataRecord requestRecordMock = mock(DataRecord.class);
            String requestId = sample.getRequestId();
            when(requestRetriever.retrieve(requestId)).thenReturn(requestRecordMock);
            when(requestConverter.convert(requestRecordMock)).thenReturn(initialRequests.get(requestId));

            sampleRecordMocks.add(sampleRecordMock);
        }

        return sampleRecordMocks;
    }

    private Sample getSample(String igoId, String reqId, Recipe recipe, List<Strand> strands, List<ProtocolType> protocolTypes) {
        Sample sample = new Sample(igoId);
        sample.setRequestId(reqId);
        sample.setRecipe(Recipe.getRecipeByValue(recipe.getValue()));
        strands.forEach(s -> sample.addStrand(s));
        protocolTypes.forEach(p -> sample.addProtocol(p));

        return sample;
    }

    private Sample getSample(String igoId, String reqId, Recipe recipe, List<Strand> strands) {
       return getSample(igoId, reqId, recipe, strands, Collections.emptyList());
    }

    private Sample getSample(String igoId, String reqId, Recipe recipe) {
        return getSample(igoId, reqId, recipe, Collections.emptyList());
    }

    class SampleMatchPredicate implements BiPredicate<Sample, Sample> {
        @Override
        public boolean test(Sample requestSample, Sample sample) {
            return requestSample.getIgoId() == sample.getIgoId()
                    && requestSample.getRequestId() == sample.getRequestId()
                    && requestSample.getRecipe() == sample.getRecipe();

        }
    }

}