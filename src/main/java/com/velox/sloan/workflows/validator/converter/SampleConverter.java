package com.velox.sloan.workflows.validator.converter;

import com.velox.api.datarecord.DataRecord;
import com.velox.api.datarecord.DataRecordManager;
import com.velox.api.datarecord.IoError;
import com.velox.api.datarecord.NotFound;
import com.velox.api.user.User;
import com.velox.api.util.ServerException;
import com.velox.sloan.workflows.LoggerAndPopupDisplayer;
import com.velox.sloan.workflows.notificator.BulkNotificator;
import com.velox.sloan.workflows.util.Utils;
import com.velox.sloan.workflows.validator.retriever.SampleRetriever;
import org.mskcc.domain.*;
import org.mskcc.domain.sample.CmoSampleInfo;
import org.mskcc.domain.sample.Sample;
import org.mskcc.domain.sample.TumorNormalType;
import org.mskcc.util.Constants;
import org.mskcc.util.VeloxConstants;

import java.rmi.RemoteException;
import java.util.*;

import static org.mskcc.util.VeloxConstants.KAPA_AGILENT_CAPTURE_PROTOCOL_1;
import static org.mskcc.util.VeloxConstants.KAPA_AGILENT_CAPTURE_PROTOCOL_2;

public class SampleConverter implements Converter<DataRecord, Sample> {
    private final User user;
    private final DataRecordManager dataRecordManager;
    private final BulkNotificator notificator;
    private SampleRetriever sampleRetriever;

    public SampleConverter(User user, DataRecordManager dataRecordManager, BulkNotificator notificator, SampleRetriever sampleRetriever) {
        this.user = user;
        this.dataRecordManager = dataRecordManager;
        this.notificator = notificator;
        this.sampleRetriever = sampleRetriever;
    }

    @Override
    public Sample convert(DataRecord sampleRecord) {
        String igoId = "";
        try {
            igoId = sampleRecord.getStringVal(Sample.SAMPLE_ID, user);
            Sample sample = new Sample(igoId);
            sample.setCmoSampleId(sampleRecord.getStringVal(Sample.OTHER_SAMPLE_ID, user));
            sample.setRequestId(sampleRecord.getStringVal(Sample.REQUEST_ID, user));

            addSampleClass(sample, sampleRecord);
            addSpecies(sample, sampleRecord);
            addRecipe(sample, sampleRecord);
            addStrandInfo(sample, sampleRecord);
            addKapaProtocol(sample, sampleRecord);
            addNimbleGenProtocol(sample, sampleRecord);
            addDnaLibraryProtocol(sample, sampleRecord);

            return sample;
        } catch (Exception e) {
            throw new SampleConvertionException(String.format("Unable to convert sample data record to sample ", igoId), e);
        }
    }

    private void addDnaLibraryProtocol(Sample sample, DataRecord sampleRecord) {
        double libVol = getLibraryVolume(sample, sampleRecord, VeloxConstants.DNA_LIBRARY_PREP_PROTOCOL_3);
        if (libVol <= 0) {
            getLibraryVolume(sample, sampleRecord, VeloxConstants.DNA_LIBRARY_PREP_PROTOCOL_2);
        }
    }

    private double getLibraryVolume(Sample sample, DataRecord rec, String protocolName) {
        List<DataRecord> DNALibPreps = new ArrayList<>();
        try {
            DNALibPreps = rec.getDescendantsOfType(protocolName, user);
        } catch (Exception e) {
        }

        if (DNALibPreps == null || DNALibPreps.size() == 0)
            return -9;

        double input = -1;
        for (DataRecord dnaLibRecord : DNALibPreps) {
            Protocol dnaLibraryPrepProtocol = new Protocol();
            Boolean real = getValidity(dnaLibRecord);
            dnaLibraryPrepProtocol.setValid(real);

            if (real)
                input = getElutionVolume(sample, protocolName, dnaLibRecord);

            dnaLibraryPrepProtocol.put(VeloxConstants.ELUTION_VOL, input);
            sample.addProtocol(protocolName, dnaLibraryPrepProtocol);
        }

        return input;
    }

    private double getElutionVolume(Sample sample, String dataRecordName, DataRecord dnaLibRecord) {
        double input = -1;

        try {
            input = dnaLibRecord.getDoubleVal(VeloxConstants.ELUTION_VOL, user);
        } catch (NullPointerException e) {
            input = -1;
            LoggerAndPopupDisplayer.logInfo(String.format("Cannot find elution vol for sample: %s Using DataRecord: %s", sample.getIgoId(), dataRecordName));
        } catch (Exception e) {
            LoggerAndPopupDisplayer.logInfo("Exception thrown while retrieving information about Elution Volume", e);
        }
        return input;
    }

    private Boolean getValidity(DataRecord n1) {
        Boolean real;
        try {
            real = n1.getBooleanVal(VeloxConstants.VALID, user);
        } catch (Exception e) {
            real = false;
        }
        return real;
    }

    private void addKapaProtocol(Sample sample, DataRecord sampleRecord) {
        sample.setKapaAgilentCaptureProtocols1(getKapaAgilentCaptureProtocol1(sample, sampleRecord));
        sample.setKapaAgilentCaptureProtocols2(getKapaAgilentCaptureProtocol2(sample, sampleRecord));
    }

    private List<KapaAgilentCaptureProtocol> getKapaAgilentCaptureProtocol2(Sample sample, DataRecord sampleRecord) {
        return getKapaAgilentCaptureProtocol(sample, sampleRecord, KAPA_AGILENT_CAPTURE_PROTOCOL_2);
    }

    private List<KapaAgilentCaptureProtocol> getKapaAgilentCaptureProtocol1(Sample sample, DataRecord sampleRecord) {
        return getKapaAgilentCaptureProtocol(sample, sampleRecord, KAPA_AGILENT_CAPTURE_PROTOCOL_1);
    }

    private List<KapaAgilentCaptureProtocol> getKapaAgilentCaptureProtocol(Sample sample, DataRecord sampleRecord, String protocolName) {
        List<KapaAgilentCaptureProtocol> kapaAgilentCaptureProtocols = new ArrayList<>();
        try {
            List<List<Map<String, Object>>> protocolFieldsList = dataRecordManager.getFieldsForDescendantsOfType(Arrays.asList(sampleRecord), protocolName, user);
            for (Map<String, Object> protocolFields : protocolFieldsList.get(0)) {
                KapaAgilentCaptureProtocol kapaAgilentCaptureProtocol = new KapaAgilentCaptureProtocol();
                kapaAgilentCaptureProtocol.setProtocolFields(protocolFields);
                kapaAgilentCaptureProtocols.add(kapaAgilentCaptureProtocol);
            }
        } catch (Exception e) {
            LoggerAndPopupDisplayer.logError(String.format("Exception thrown while retrieving information about %s for sample: %s", protocolName, sample.getIgoId()), e);
        }

        return kapaAgilentCaptureProtocols;
    }

    private void addNimbleGenProtocol(Sample sample, DataRecord sampleRecord) {
        try {
            List<DataRecord> nimbProtocols = sampleRecord.getDescendantsOfType(VeloxConstants.NIMBLE_GEN_HYB_PROTOCOL, user);
            List<Object> valid = dataRecordManager.getValueList(nimbProtocols, VeloxConstants.VALID, user);
            List<Object> igoId = dataRecordManager.getValueList(nimbProtocols, VeloxConstants.SAMPLE_ID, user);
            List<Object> creationDate = dataRecordManager.getValueList(nimbProtocols, "DateCreated", user);

            for (int i = 0; i < nimbProtocols.size(); i++) {
                NimbleGenHybProtocol nimbleGenHybProtocol = new NimbleGenHybProtocol();
                nimbleGenHybProtocol.setValid((boolean) valid.get(i));
                nimbleGenHybProtocol.setCreationDate(new Date((long) creationDate.get(i)));
                nimbleGenHybProtocol.setIgoSampleId((String) igoId.get(i));

                sample.addNimbleGenHybProtocol(nimbleGenHybProtocol);
            }

        } catch (Exception e) {

        }
    }

    private void addRecipe(Sample sample, DataRecord sampleRecord) throws Exception {
        String recipeName = sampleRecord.getStringVal(VeloxConstants.RECIPE, user);

        try {
            Recipe recipe = Recipe.getRecipeByValue(recipeName);
            sample.setRecipe(recipe.getValue());
        } catch (Recipe.UnsupportedRecipeException e) {
            String message = String.format("Unsupported recipe: %s for sample: %s", Utils.getFormattedValue(recipeName), sample.getIgoId());
            notificator.addMessage(sample.getRequestId(), message);
            LoggerAndPopupDisplayer.logError(message, e);
        } catch (Recipe.EmptyRecipeException e) {
            String message = String.format("Empty recipe for sample: %s", sample.getIgoId());
            notificator.addMessage(sample.getRequestId(), message);
            LoggerAndPopupDisplayer.logError(message, e);
        }
    }

    private void addSampleClass(Sample sample, DataRecord sampleRecord) throws Exception {
        sample.setSampleClass(sampleRecord.getStringVal(VeloxConstants.CMO_SAMPLE_CLASS, user));
        TumorNormalType tumorNormalType = TumorNormalType.getByValue(sampleRecord.getStringVal(VeloxConstants.TUMOR_OR_NORMAL, user));
        sample.setTumorNormalType(tumorNormalType);

        addSampleInfo(sample);
    }

    private void addSampleInfo(Sample sample) throws Exception {
        CmoSampleInfo cmoSampleInfo = sampleRetriever.getCmoSampleInfo(sample.getIgoId());
        sample.setCmoSampleInfo(cmoSampleInfo);
    }


    private void addSpecies(Sample sample, DataRecord sampleRecord) throws NotFound, RemoteException {
        sample.put(Constants.SPECIES, sampleRecord.getStringVal(VeloxConstants.SPECIES, user));
        sample.put(Constants.SAMPLE_TYPE, sampleRecord.getStringVal(VeloxConstants.SPECIMEN_TYPE, user));
    }

    private void addStrandInfo(Sample sample, DataRecord sampleRecord) throws Exception {
        processTruSeqRnaProtocol(sample, sampleRecord);
        if (Arrays.asList(sampleRecord.getChildrenOfType(VeloxConstants.TRU_SEQ_RNA_SM_RNA_PROTOCOL_4, user)).size() > 0) {
            sample.addStrand(Strand.EMPTY);
            sample.addProtocol(ProtocolType.TRU_SEQ_RNA_SM_RNA_PROTOCOL_4);
        }
        if (checkValidBool(Arrays.asList(sampleRecord.getChildrenOfType(VeloxConstants.TRU_SEQ_RIBO_DEPLETE_PROTOCOL_1, user)), dataRecordManager, user)) {
            sample.addStrand(Strand.REVERSE);
            sample.addProtocol(ProtocolType.TRU_SEQ_RIBO_DEPLETE_PROTOCOL_1);
        }
        if (checkValidBool(Arrays.asList(sampleRecord.getChildrenOfType(VeloxConstants.TRU_SEQ_RNA_FUSION_PROTOCOL_1, user)), dataRecordManager, user)) {
            sample.addStrand(Strand.NONE);
            sample.addProtocol(ProtocolType.TRU_SEQ_RNA_FUSION_PROTOCOL_1);
        }
        if (checkValidBool(Arrays.asList(sampleRecord.getChildrenOfType(VeloxConstants.SMAR_TER_AMPLIFICATION_PROTOCOL_1, user)), dataRecordManager, user)) {
            sample.addStrand(Strand.NONE);
            sample.addProtocol(ProtocolType.SMAR_TER_AMPLIFICATION_PROTOCOL_1);
        }
        if (checkValidBool(Arrays.asList(sampleRecord.getChildrenOfType(VeloxConstants.KAPA_MRNA_STRANDED_SEQ_PROTOCOL_1, user)), dataRecordManager, user)) {
            sample.addStrand(Strand.REVERSE);
            sample.addProtocol(ProtocolType.KAPA_MRNA_STRANDED_SEQ_PROTOCOL_1);
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
            sample.addProtocol(ProtocolType.TRU_SEQ_STRANDING);
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
