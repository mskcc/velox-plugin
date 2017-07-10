package com.velox.sloan.workflows.validator.converter;

import com.velox.api.datarecord.DataRecord;
import com.velox.api.datarecord.DataRecordManager;
import com.velox.api.datarecord.IoError;
import com.velox.api.datarecord.NotFound;
import com.velox.api.user.User;
import com.velox.api.util.ServerException;
import com.velox.sloan.cmo.staticstrings.datatypes.DT_Sample;
import com.velox.sloan.workflows.LoggerAndPopupDisplayer;
import com.velox.sloan.workflows.notificator.Notificator;
import org.mskcc.domain.Protocol;
import org.mskcc.domain.Recipe;
import org.mskcc.domain.Strand;
import org.mskcc.domain.sample.CmoSampleInfo;
import org.mskcc.domain.sample.Sample;
import org.mskcc.domain.sample.TumorNormalType;
import org.mskcc.util.Constants;
import org.mskcc.util.VeloxConstants;

import java.rmi.RemoteException;
import java.util.*;

public class SampleConverter implements Converter<DataRecord, Sample> {
    private final User user;
    private final DataRecordManager dataRecordManager;
    private final Notificator notificator;

    public SampleConverter(User user, DataRecordManager dataRecordManager, Notificator notificator) {
        this.user = user;
        this.dataRecordManager = dataRecordManager;
        this.notificator = notificator;
    }

    @Override
    public Sample convert(DataRecord sampleRecord) {
        String igoId = "";
        try {
            igoId = sampleRecord.getStringVal(DT_Sample.SAMPLE_ID, user);
            Sample sample = new Sample(igoId);
            sample.setRequestId(sampleRecord.getStringVal(DT_Sample.REQUEST_ID, user));
            fillSampleClass(sample, sampleRecord);
            addSpecies(sample, sampleRecord);
            addRecipe(sample, sampleRecord);
            addStrandInfo(sample, sampleRecord);

            return sample;
        } catch (Exception e) {
            throw new SampleConvertionException(String.format("Unable to convert sample data record to sample ", igoId), e);
        }
    }

    private void addRecipe(Sample sample, DataRecord sampleRecord) throws Exception {
        String recipeName = sampleRecord.getStringVal(VeloxConstants.RECIPE, user);

        try {
            Recipe recipe = Recipe.getRecipeByValue(recipeName);
            sample.setRecipe(recipe);
        } catch (Recipe.UnsupportedRecipeException e) {
            notificator.addMessage(sample.getRequestId(), String.format("Sample: %s - %s", sample.getIgoId(), e.getMessage()));
        }
    }

    private void fillSampleClass(Sample sample, DataRecord sampleRecord) throws Exception {
        sample.setSampleClass(sampleRecord.getStringVal(VeloxConstants.CMO_SAMPLE_CLASS, user));
        TumorNormalType tumorNormalType = TumorNormalType.getByValue(sampleRecord.getStringVal(VeloxConstants.TUMOR_OR_NORMAL, user));
        sample.setTumorNormalType(tumorNormalType);

        fillInSampleInfo(sample, sampleRecord);
    }

    private void fillInSampleInfo(Sample sample, DataRecord sampleRecord) throws Exception {
        List<List<Map<String, Object>>> sampleInfos = dataRecordManager.getFieldsForChildrenOfType(Collections.singletonList(sampleRecord), VeloxConstants.SAMPLE_CMO_INFO_RECORDS, user);
        if (sampleInfos.size() > 0 && sampleInfos.get(0).size() > 0) {
            Map<String, Object> sampleInfo = sampleInfos.get(0).get(0);

            CmoSampleInfo cmoSampleInfo = sample.getCmoSampleInfo();
            cmoSampleInfo.setSampleClass(String.valueOf(sampleInfo.get(VeloxConstants.CMO_SAMPLE_CLASS)));
            TumorNormalType tumorNormalType = TumorNormalType.getByValue(String.valueOf(sampleInfo.get(VeloxConstants.TUMOR_OR_NORMAL)));
            cmoSampleInfo.setTumorNormalType(tumorNormalType);
        }
    }

    private void addSpecies(Sample sample, DataRecord sampleRecord) throws NotFound, RemoteException {
        sample.put(Constants.SPECIES, sampleRecord.getStringVal(VeloxConstants.SPECIES, user));
        sample.put(Constants.SAMPLE_TYPE, sampleRecord.getStringVal(VeloxConstants.SPECIMEN_TYPE, user));
    }

    private void addStrandInfo(Sample sample, DataRecord sampleRecord) throws Exception {
        processTruSeqRnaProtocol(sample, sampleRecord);
        if (Arrays.asList(sampleRecord.getChildrenOfType(VeloxConstants.TRU_SEQ_RNA_SM_RNA_PROTOCOL_4, user)).size() > 0) {
            sample.addStrand(Strand.EMPTY);
            sample.addProtocol(Protocol.TRU_SEQ_RNA_SM_RNA_PROTOCOL_4);
        }
        if (checkValidBool(Arrays.asList(sampleRecord.getChildrenOfType(VeloxConstants.TRU_SEQ_RIBO_DEPLETE_PROTOCOL_1, user)), dataRecordManager, user)) {
            sample.addStrand(Strand.REVERSE);
            sample.addProtocol(Protocol.TRU_SEQ_RIBO_DEPLETE_PROTOCOL_1);
        }
        if (checkValidBool(Arrays.asList(sampleRecord.getChildrenOfType(VeloxConstants.TRU_SEQ_RNA_FUSION_PROTOCOL_1, user)), dataRecordManager, user)) {
            sample.addStrand(Strand.NONE);
            sample.addProtocol(Protocol.TRU_SEQ_RNA_FUSION_PROTOCOL_1);
        }
        if (checkValidBool(Arrays.asList(sampleRecord.getChildrenOfType(VeloxConstants.SMAR_TER_AMPLIFICATION_PROTOCOL_1, user)), dataRecordManager, user)) {
            sample.addStrand(Strand.NONE);
            sample.addProtocol(Protocol.SMAR_TER_AMPLIFICATION_PROTOCOL_1);
        }
        if (checkValidBool(Arrays.asList(sampleRecord.getChildrenOfType(VeloxConstants.KAPA_MRNA_STRANDED_SEQ_PROTOCOL_1, user)), dataRecordManager, user)) {
            sample.addStrand(Strand.REVERSE);
            sample.addProtocol(Protocol.KAPA_MRNA_STRANDED_SEQ_PROTOCOL_1);
        }
    }

    private void processTruSeqRnaProtocol(Sample sample, DataRecord sampleRecord) throws IoError, RemoteException {
        List<DataRecord> truSeqRnaProtocolChildren = Arrays.asList(sampleRecord.getChildrenOfType(VeloxConstants.TRU_SEQ_RNA_PROTOCOL, user));
        if (checkValidBool(truSeqRnaProtocolChildren, dataRecordManager, user)) {
            for (DataRecord rnaProtocol : truSeqRnaProtocolChildren) {
                String experimentId = "";
                try {
                    if (getBoolean(rnaProtocol, VeloxConstants.VALID)) {
                        experimentId = rnaProtocol.getStringVal(VeloxConstants.EXPERIMENT_ID, user);
                        List<DataRecord> rnaExp = dataRecordManager.queryDataRecords(VeloxConstants.TRU_SEQ_RNA_EXPERIMENT, "ExperimentId='" + experimentId + "'", user);
                        if (rnaExp.size() != 0)
                            addStrands(sample, rnaExp);
                    }
                } catch (Exception e) {
                    LoggerAndPopupDisplayer.logError(String.format("Unable to retirieve information for Tru Seq Rna Experiment %s", experimentId));
                }
            }
        }
    }

    private void addStrands(Sample sample, List<DataRecord> rnaExp) throws ServerException, RemoteException {
        for (Object stranding : dataRecordManager.getValueList(rnaExp, VeloxConstants.TRU_SEQ_STRANDING, user)) {
            sample.addStrand(getStrand(stranding));
            sample.addProtocol(Protocol.TRU_SEQ_STRANDING);
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
