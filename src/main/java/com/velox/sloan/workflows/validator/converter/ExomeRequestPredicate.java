package com.velox.sloan.workflows.validator.converter;

import com.velox.sloan.workflows.LoggerAndPopupDisplayer;
import org.mskcc.domain.Request;
import org.mskcc.util.Constants;
import org.mskcc.util.VeloxConstants;

import java.util.function.Predicate;

class ExomeRequestPredicate implements Predicate<Request> {
    @Override
    public boolean test(Request request) {
        LoggerAndPopupDisplayer.logInfo("request name: " + request.getName());
        return request.getName().contains(VeloxConstants.EXOME) || request.getName().equals(Constants.WES) || hasKapaProtocol1(request);
    }

    private boolean hasKapaProtocol1(Request request) {
        return request.getSamples().values().stream()
                .anyMatch(s -> s.getKapaAgilentCaptureProtocols1().size() != 0);
    }
}
