package com.velox.sloan.workflows.validator.converter;

import org.mskcc.domain.Request;

import java.util.function.Predicate;

class ImpactRequestPredicate implements Predicate<Request> {
    @Override
    public boolean test(Request request) {
        return request.getName().contains("PACT") || hasNimbleProtocol(request) || request.isInnovationProject();
    }

    private boolean hasNimbleProtocol(Request request) {
        return request.getSamples().values().stream()
                .anyMatch(s -> s.getNimbleGenHybProtocols().size() != 0);
    }
}
