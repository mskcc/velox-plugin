package com.velox.sloan.workflows.validator;

import com.velox.sloan.workflows.notificator.BulkNotificator;
import org.apache.commons.lang3.StringUtils;
import org.mskcc.domain.Request;
import org.mskcc.domain.XenograftClass;
import org.mskcc.domain.sample.Sample;
import org.mskcc.util.Constants;

import java.util.List;
import java.util.stream.Collectors;

public class SpeciesValidator implements Validator {
    private BulkNotificator notificator;

    public SpeciesValidator(BulkNotificator notificator) {
        this.notificator = notificator;
    }

    @Override
    public boolean isValid(Request request) {
        return allSamplesHaveSpeciesSet(request) && areAllSampleSpeciesTheSame(request);
    }

    private boolean allSamplesHaveSpeciesSet(Request request) {
        List<Sample> samplesWithEmptySpecies = getSamplesWithEmptySpecies(request);
        return samplesWithEmptySpecies.size() == 0;
    }

    private List<Sample> getSamplesWithEmptySpecies(Request request) {
        return request.getSamples().values().stream()
                .filter(s -> StringUtils.isEmpty(s.get(Constants.SPECIES))).collect(Collectors.toList());
    }

    @Override
    public BulkNotificator getBulkNotificator() {
        return notificator;
    }

    private boolean areAllSampleSpeciesTheSame(Request request) {
        return request.getSamples().values().stream()
                .map(s -> s.get(Constants.SPECIES))
                .distinct()
                .count() <= 1;
    }

    @Override
    public String getMessage(Request request) {
        List<Sample> samplesWithEmptySpecies = getSamplesWithEmptySpecies(request);
        if (samplesWithEmptySpecies.size() > 0)
            return String.format("Samples: %s have empty species", StringUtils.join(samplesWithEmptySpecies, ","));

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
