package com.velox.sloan.workflows.validator;

import com.velox.api.datarecord.DataRecord;
import com.velox.api.datarecord.DataRecordManager;
import com.velox.api.datarecord.IoError;
import com.velox.api.datarecord.NotFound;
import com.velox.api.user.User;
import com.velox.api.util.ServerException;
import com.velox.sloan.cmo.staticstrings.datatypes.DT_Sample;
import com.velox.sloan.workflows.notificator.Notificator;
import org.mskcc.domain.Recipe;
import org.mskcc.domain.Request;
import org.mskcc.domain.Sample;
import org.mskcc.domain.Strand;
import org.mskcc.util.Constants;
import org.mskcc.util.VeloxConstants;

import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class StrandValidator extends Validator {
    private final User user;
    private final DataRecordManager dataRecordManager;
    private final Map<String, DataRecord> sampleIgoIdToRecord;

    public StrandValidator(Notificator notificator, User user, DataRecordManager dataRecordManager, Map<String, DataRecord> sampleIgoIdToRecord) {
        super(notificator);
        this.user = user;
        this.dataRecordManager = dataRecordManager;
        this.sampleIgoIdToRecord = sampleIgoIdToRecord;
    }

    @Override
    boolean isValid(Request request) {
        return getNumberOfNonEmptyStrands(request) > 0;
    }

    private long getNumberOfNonEmptyStrands(Request request) {
        return request.getStrands().stream().filter(s -> s != Strand.EMPTY).count();
    }

    @Override
    String getMessage(Request request) {
        return String.format("Request %s has no strand information", request.getId());
    }

    @Override
    String getName() {
        return "Strand validator for RNASeq requests";
    }

    @Override
    Map<String, Request> updateRequests(Map<String, Request> requests) throws Exception {
        addRecipes(requests);
        return addStrandInfo(requests);
    }

    private void addRecipes(Map<String, Request> requests) throws Exception {
        for (Request request : requests.values()) {
            addRecipes(request);
        }
    }

    private void addRecipes(Request request) throws Exception {
        for (Sample sample : request.getSamples().values()) {
            addRecipe(sample);
        }
    }

    private void addRecipe(Sample sample) throws Exception {
        String igoId = sample.getIgoId();
        String recipeName = sampleIgoIdToRecord.get(igoId).getStringVal(DT_Sample.RECIPE, user);

        try {
            Recipe recipe = Recipe.getRecipeByValue(recipeName);
            sample.setRecipe(recipe);
        } catch (Recipe.UnsupportedRecipeException e) {
            LoggerAndPopup.logInfo(String.format("Recipe: %s is not supported", recipeName));
        }
    }

    @Override
    boolean shouldValidate(Request request) {
        return isRNASeqRequest(request);
    }

    private boolean isRNASeqRequest(Request request) {
        return Objects.equals(request.getRequestType(), Constants.RNASEQ)
                || (request.getSamplesRecipes().size() == 1 && request.getSamplesRecipes().get(0) == Recipe.SMARTER_AMP_SEQ);
    }

    private Map<String, Request> addStrandInfo(Map<String, Request> requests) throws Exception {
        for (Request request : requests.values()) {
            for (DataRecord sampleRecord : sampleIgoIdToRecord.values())
                addStrandInfo(request, sampleRecord);
        }

        return requests;
    }

    private void addStrandInfo(Request request, DataRecord sampleRecord) throws Exception {
        processTruSeqRnaProtocol(request, sampleRecord);
        if (Arrays.asList(sampleRecord.getChildrenOfType(VeloxConstants.TRU_SEQ_RNA_SM_RNA_PROTOCOL_4, user)).size() > 0) {
            request.addStrand(Strand.EMPTY);
            request.setRequestType(Constants.RNASEQ);
        }
        if (checkValidBool(Arrays.asList(sampleRecord.getChildrenOfType(VeloxConstants.TRU_SEQ_RIBO_DEPLETE_PROTOCOL_1, user)), dataRecordManager, user)) {
            request.addStrand(Strand.REVERSE);
            request.setRequestType(Constants.RNASEQ);
        }
        if (checkValidBool(Arrays.asList(sampleRecord.getChildrenOfType(VeloxConstants.TRU_SEQ_RNA_FUSION_PROTOCOL_1, user)), dataRecordManager, user)) {
            request.addStrand(Strand.NONE);
            request.setRequestType(Constants.RNASEQ);
        }
        if (checkValidBool(Arrays.asList(sampleRecord.getChildrenOfType(VeloxConstants.SMAR_TER_AMPLIFICATION_PROTOCOL_1, user)), dataRecordManager, user)) {
            request.addStrand(Strand.NONE);
            request.setRequestType(Constants.RNASEQ);
        }
        if (checkValidBool(Arrays.asList(sampleRecord.getChildrenOfType(VeloxConstants.KAPA_MRNA_STRANDED_SEQ_PROTOCOL_1, user)), dataRecordManager, user)) {
            request.addStrand(Strand.REVERSE);
            request.setRequestType(Constants.RNASEQ);
        }
    }

    private void processTruSeqRnaProtocol(Request request, DataRecord sampleRecord) throws IoError, RemoteException {
        List<DataRecord> truSeqRnaProtocolChildren = Arrays.asList(sampleRecord.getChildrenOfType(VeloxConstants.TRU_SEQ_RNA_PROTOCOL, user));
        if (checkValidBool(truSeqRnaProtocolChildren, dataRecordManager, user)) {
            for (DataRecord rnaProtocol : truSeqRnaProtocolChildren) {
                try {
                    if (getBoolean(rnaProtocol, VeloxConstants.VALID)) {
                        String experimentId = rnaProtocol.getStringVal(VeloxConstants.EXPERIMENT_ID, user);
                        List<DataRecord> rnaExp = dataRecordManager.queryDataRecords(VeloxConstants.TRU_SEQ_RNA_EXPERIMENT, "ExperimentId='" + experimentId + "'", user);
                        if (rnaExp.size() != 0)
                            addStrands(request, rnaExp);
                    }
                } catch (Exception e) {
                }
            }
        }
    }

    private void addStrands(Request request, List<DataRecord> rnaExp) throws ServerException, RemoteException {
        for (Object stranding : dataRecordManager.getValueList(rnaExp, VeloxConstants.TRU_SEQ_STRANDING, user)) {
            request.addStrand(getStrand(stranding));
            request.setRequestType(Constants.RNASEQ);
        }
    }

    private Strand getStrand(Object stranding) {
        return Objects.equals(String.valueOf(stranding), Constants.STRANDED) ? Strand.REVERSE : Strand.NONE;
    }

    private Boolean checkValidBool(List<DataRecord> recs, DataRecordManager drm, User apiUser) {
        if (recs == null || recs.size() == 0) {
            return false;
        }

        try {
            List<Object> valids = drm.getValueList(recs, VeloxConstants.VALID, apiUser);
            for (Object val : valids) {
                if (String.valueOf(val).equals(VeloxConstants.TRUE)) {
                    return true;
                }
            }
        } catch (Exception e) {
        }

        return false;
    }

    private boolean getBoolean(DataRecord dataRecordRequest, String fieldName) {
        try {
            return dataRecordRequest.getBooleanVal(fieldName, user);
        } catch (NullPointerException e) {
            return false;
        } catch (NotFound | RemoteException e) {
        }

        return false;
    }
}
