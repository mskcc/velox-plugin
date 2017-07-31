package com.velox.sloan.workflows.validator;

import com.velox.sloan.workflows.notificator.BulkNotificator;
import org.apache.commons.lang3.StringUtils;
import org.mskcc.domain.Protocol;
import org.mskcc.domain.Request;
import org.mskcc.domain.RequestType;
import org.mskcc.domain.sample.Sample;
import org.mskcc.util.VeloxConstants;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

public class DnaLibraryProtocolValidator implements Validator {
    private final BulkNotificator bulkNotificator;
    private final Predicate<Sample> dnaLibraryProtocolValidPredicate;
    private final SamplesValidator samplesValidator;

    public DnaLibraryProtocolValidator(BulkNotificator bulkNotificator, Predicate<Sample> dnaLibraryProtocolValidPredicate, SamplesValidator samplesValidator) {
        this.bulkNotificator = bulkNotificator;
        this.dnaLibraryProtocolValidPredicate = dnaLibraryProtocolValidPredicate;
        this.samplesValidator = samplesValidator;
    }

    @Override
    public boolean isValid(Request request) {
        return samplesValidator.isValid(request, dnaLibraryProtocolValidPredicate);
    }

    @Override
    public BulkNotificator getBulkNotificator() {
        return bulkNotificator;
    }

    @Override
    public String getMessage(Request request) {
        Set<Sample> nonValidSamples = samplesValidator.getNonValidSamples(request, dnaLibraryProtocolValidPredicate);
        return String.format("Request: %s has samples with incorrect elution volume in Dna Library Protocol %s", StringUtils.join(nonValidSamples, ","));
    }

    @Override
    public String getName() {
        return "Dna Library Protocol Validator";
    }

    @Override
    public boolean shouldValidate(Request request) {
        return request.getRequestType() == RequestType.IMPACT || request.getRequestType() == RequestType.EXOME;
    }

    static class DnaLibraryProtocolValidPredicate implements Predicate<Sample> {
        @Override
        public boolean test(Sample sample) {
            Collection<Protocol> dnaLibProtocol2 = sample.getProtocols().get(VeloxConstants.DNA_LIBRARY_PREP_PROTOCOL_2);
            Collection<Protocol> dnaLibProtocol3 = sample.getProtocols().get(VeloxConstants.DNA_LIBRARY_PREP_PROTOCOL_3);

            Set<Protocol> protocols = new HashSet<>();
            if (dnaLibProtocol2 != null)
                protocols.addAll(dnaLibProtocol2);
            if (dnaLibProtocol3 != null)
                protocols.addAll(dnaLibProtocol3);

            return protocols.stream()
                    .allMatch(d -> d.isValid() != null && d.isValid() && d.getProtocolFields().containsKey(VeloxConstants.ELUTION_VOL));
        }
    }
}
