package com.velox.sloan.workflows.validator.converter;

import com.velox.api.datarecord.DataRecord;
import com.velox.sloan.workflows.LoggerAndPopupDisplayer;
import com.velox.sloan.workflows.validator.retriever.RequestRetriever;
import org.mskcc.domain.Request;
import org.mskcc.domain.RequestType;
import org.mskcc.domain.Strand;
import org.mskcc.domain.sample.Sample;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class SampleRecordsToRequestsConverter implements Converter<Collection<DataRecord>, Map<String, Request>> {
    private final Converter<DataRecord, Sample> converter;
    private final Converter<DataRecord, Request> requestConverter;
    private final RequestRetriever requestRetriever;
    private final Predicate<Request> rnaSeqRequestPredicate;

    public SampleRecordsToRequestsConverter(Converter<DataRecord, Sample> sampleConverter,
                                            RequestRetriever requestRetriever,
                                            Converter<DataRecord, Request> requestConverter,
                                            Predicate<Request> rnaSeqRequestPredicate) {
        this.converter = sampleConverter;
        this.requestConverter = requestConverter;
        this.requestRetriever = requestRetriever;
        this.rnaSeqRequestPredicate = rnaSeqRequestPredicate;
    }

    @Override
    public Map<String, Request> convert(Collection<DataRecord> sampleRecords) {
        Map<String, Request> requests = new HashMap<>();
        for (Sample sample : getSamples(sampleRecords)) {
            try {
                String reqId = sample.getRequestId();
                requests.putIfAbsent(reqId, getRequest(reqId));
                Request request = requests.get(reqId);
                request.putSampleIfAbsent(sample);
            } catch(Exception e) {
                LoggerAndPopupDisplayer.logError(String.format("Cannot process sample: %s", sample.getIgoId()));
            }
        }

        for (Request request : requests.values()) {
            setRequestType(request);
            setStrands(request);
        }

        return requests;
    }

    private void setStrands(Request request) {
        Set<Strand> strands = request.getSamples().values().stream()
                .flatMap(s -> s.getStrands().stream())
                .collect(Collectors.toSet());
        request.setStrands(strands);
    }

    private void setRequestType(Request request) {
        if(rnaSeqRequestPredicate.test(request))
            request.setRequestType(RequestType.RNASEQ);
    }

    private Request getRequest(String reqId) throws Exception {
        return requestConverter.convert(requestRetriever.retrieve(reqId));
    }

    private List<Sample> getSamples(Collection<DataRecord> sampleRecords) {
        return sampleRecords.stream()
                .map(s -> converter.convert(s))
                .collect(Collectors.toList());
    }
}
