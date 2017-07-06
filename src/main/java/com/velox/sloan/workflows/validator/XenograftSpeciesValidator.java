package com.velox.sloan.workflows.validator;

import com.velox.api.datarecord.DataRecord;
import com.velox.api.user.User;
import com.velox.sloan.workflows.notificator.Notificator;
import org.mskcc.domain.Request;
import org.mskcc.domain.RequestSpecies;
import org.mskcc.domain.Sample;
import org.mskcc.util.Constants;

import java.util.Map;

public class XenograftSpeciesValidator extends SpeciesValidator {
    public XenograftSpeciesValidator(Notificator notificator, User user, Map<String, DataRecord> sampleIgoIdToRecord) {
        super(notificator, user, sampleIgoIdToRecord);
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
