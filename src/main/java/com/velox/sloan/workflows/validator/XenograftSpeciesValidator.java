package com.velox.sloan.workflows.validator;

import com.velox.sloan.workflows.notificator.BulkNotificator;
import org.mskcc.domain.Request;
import org.mskcc.domain.RequestSpecies;
import org.mskcc.domain.sample.Sample;
import org.mskcc.util.Constants;

public class XenograftSpeciesValidator extends SpeciesValidator {
    public XenograftSpeciesValidator(BulkNotificator notificator) {
        super(notificator);
    }

    @Override
    public boolean isValid(Request request) {
        return areAllSamplesHumanOrXenograft(request);
    }

    private boolean areAllSamplesHumanOrXenograft(Request request) {
        return request.getSamples().values().stream()
                .allMatch(this::isHumanOrXenograftSample);
    }

    private boolean isHumanOrXenograftSample(Sample sample) {
        RequestSpecies sampleSpecies = RequestSpecies.getSpeciesByValue(sample.get(Constants.SPECIES));
        return sampleSpecies == RequestSpecies.HUMAN || sampleSpecies == RequestSpecies.XENOGRAFT;
    }

    @Override
    public boolean shouldValidate(Request request) {
        return isXenograftRequest(request);
    }
}
