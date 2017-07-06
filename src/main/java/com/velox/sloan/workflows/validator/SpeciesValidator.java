package com.velox.sloan.workflows.validator;

import com.velox.api.datarecord.DataRecord;
import com.velox.api.datarecord.NotFound;
import com.velox.api.user.User;
import com.velox.sloan.workflows.notificator.Notificator;
import org.mskcc.domain.Request;
import org.mskcc.domain.Sample;
import org.mskcc.domain.XenograftClass;
import org.mskcc.util.Constants;
import org.mskcc.util.VeloxConstants;

import java.rmi.RemoteException;
import java.util.Map;
import java.util.stream.Collectors;

public class SpeciesValidator extends Validator {
    private final User user;
    private final Map<String, DataRecord> sampleIgoIdToRecord;

    public SpeciesValidator(Notificator notificator, User user, Map<String, DataRecord> sampleIgoIdToRecord) {
        super(notificator);
        this.user = user;
        this.sampleIgoIdToRecord = sampleIgoIdToRecord;
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
    public Map<String, Request> updateRequests(Map<String, Request> requests) throws Exception {
        for (Request request : requests.values()) {
            addSpecies(request);
        }

        return requests;
    }

    private void addSpecies(Request request) throws NotFound, RemoteException {
        for (Sample sample : request.getSamples().values()) {
            addSpecies(sample);
        }
    }

    private void addSpecies(Sample sample) throws NotFound, RemoteException {
        DataRecord sampleRecord = sampleIgoIdToRecord.get(sample.getIgoId());
        sample.put(Constants.SPECIES, sampleRecord.getStringVal(VeloxConstants.SPECIES, user));
        sample.put(Constants.SAMPLE_TYPE, sampleRecord.getStringVal(VeloxConstants.SPECIMEN_TYPE, user));
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
