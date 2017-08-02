package com.velox.sloan.workflows.validator.converter;

import org.apache.commons.lang3.StringUtils;
import org.mskcc.domain.Request;
import org.mskcc.domain.sample.Sample;
import org.mskcc.util.Constants;
import org.mskcc.util.VeloxConstants;

import java.util.function.Predicate;

class ExomeRequestPredicate implements Predicate<Request> {
    @Override
    public boolean test(Request request) {
        return nameConstainsExome(request) || isWESName(request) || hasKapaProtocol(request);
    }

    private boolean nameConstainsExome(Request request) {
        return StringUtils.containsIgnoreCase(request.getName(), VeloxConstants.EXOME);
    }

    private boolean isWESName(Request request) {
        return request.getName().equalsIgnoreCase(Constants.WES);
    }

    private boolean hasKapaProtocol(Request request) {
        return request.getSamples().values().stream()
                .anyMatch(s -> hasKapaProtocol(s));
    }

    private boolean hasKapaProtocol(Sample sample) {
        return sample.getKapaAgilentCaptureProtocols1().size() != 0 || sample.getKapaAgilentCaptureProtocols2().size() != 0;
    }
}
