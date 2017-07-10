package com.velox.sloan.workflows.validator;

import com.velox.sloan.workflows.notificator.Notificator;
import org.mskcc.domain.Request;
import org.mskcc.domain.XenograftClass;
import org.mskcc.util.Constants;

import java.util.stream.Collectors;

public class SpeciesValidator extends Validator {
    public SpeciesValidator(Notificator notificator) {
        super(notificator);
    }

    @Override
    public boolean isValid(Request request) {
        return areAllSampleSpeciesTheSame(request);
    }

    private boolean areAllSampleSpeciesTheSame(Request request) {
        return request.getSamples().values().stream()
                .map(s -> s.get(Constants.SPECIES))
                .distinct()
                .count() <= 1;
    }

    @Override
    public String getMessage(Request request) {
        String species = request.getSamples().values().stream()
                .map(s -> s.get(Constants.SPECIES))
                .distinct()
                .collect(Collectors.joining(","));
        return String.format("Xenograft request: %s should have only samples with Human species. Current samples' species: %s", request.getId(), species);
    }

    @Override
    public String getName() {
        return "Species validator for Xenograft request";
    }

    @Override
    public boolean shouldValidate(Request request) {
        return !isXenograftRequest(request);
    }

    boolean isXenograftRequest(Request request) {
        return request.getSamples().values().stream()
                .anyMatch(s -> XenograftClass.containsValue(s.get(Constants.SAMPLE_TYPE)));
    }
}
