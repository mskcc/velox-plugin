package com.velox.sloan.workflows.validator.converter;

import org.apache.commons.lang3.StringUtils;
import org.mskcc.domain.Request;
import org.mskcc.util.Constants;

import java.util.function.Predicate;

class ImpactRequestPredicate implements Predicate<Request> {
    @Override
    public boolean test(Request request) {
        return StringUtils.containsIgnoreCase(request.getName(), Constants.PACT) || hasNimbleProtocol(request) || request.isInnovation();
    }

    private boolean hasNimbleProtocol(Request request) {
        return request.getSamples().values().stream()
                .anyMatch(s -> s.getNimbleGenHybProtocols().size() != 0);
    }
}
