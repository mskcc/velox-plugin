package com.velox.sloan.workflows.validator;

import com.velox.sloan.workflows.notificator.BulkNotificator;
import org.mskcc.domain.Request;
import org.mskcc.domain.RequestType;
import org.mskcc.domain.sample.Sample;

import java.util.function.Predicate;

public class NimleGenProtocolValidator implements Validator {
    private final BulkNotificator notificator;
    private final Predicate<Sample> nimbGenProtocolValidPredicate;
    private final SamplesValidator samplesValidator;
    private String errorMessage = "";

    public NimleGenProtocolValidator(BulkNotificator notificator, Predicate<Sample> nimbGenProtocolValidPredicate, SamplesValidator samplesValidator) {
        this.notificator = notificator;
        this.nimbGenProtocolValidPredicate = nimbGenProtocolValidPredicate;
        this.samplesValidator = samplesValidator;
    }

    @Override
    public boolean isValid(Request request) {
        try {
            return samplesValidator.isValid(request, nimbGenProtocolValidPredicate);
        } catch (InvalidNimbleGenProtocolException e) {
            errorMessage = e.getMessage();
        }

        return false;
    }

    @Override
    public BulkNotificator getBulkNotificator() {
        return notificator;
    }

    @Override
    public String getMessage(Request request) {
        return String.format("Request: %s doesn't have correctly set NimbleGen Protocols: %s", errorMessage);
    }

    @Override
    public String getName() {
        return "NimbleGen Protocol Validator";
    }

    @Override
    public boolean shouldValidate(Request request) {
        return request.getRequestType() == RequestType.IMPACT;
    }
}
