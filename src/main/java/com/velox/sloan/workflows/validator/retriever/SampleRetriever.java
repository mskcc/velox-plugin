package com.velox.sloan.workflows.validator.retriever;

import com.velox.api.datarecord.DataRecord;
import com.velox.api.datarecord.DataRecordManager;
import com.velox.api.user.User;
import com.velox.sloan.workflows.LoggerAndPopupDisplayer;
import com.velox.sloan.workflows.validator.converter.CmoSampleInfoConverter;
import org.mskcc.domain.sample.CmoSampleInfo;
import org.mskcc.util.VeloxConstants;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class SampleRetriever {
    private Map<String, DataRecord> sampleIgoIdToRecord;
    private DataRecordManager dataRecordManager;
    private User user;
    private CmoSampleInfoConverter cmoSampleInfoConverter;

    public SampleRetriever(Map<String, DataRecord> sampleIgoIdToRecord, DataRecordManager dataRecordManager, User user, CmoSampleInfoConverter cmoSampleInfoConverter) {
        this.sampleIgoIdToRecord = sampleIgoIdToRecord;
        this.dataRecordManager = dataRecordManager;
        this.user = user;
        this.cmoSampleInfoConverter = cmoSampleInfoConverter;
    }

    public CmoSampleInfo getCmoSampleInfo(String igoId) {
        DataRecord sampleRecord = sampleIgoIdToRecord.get(igoId);
        try {
            List<List<Map<String, Object>>> sampleInfos = dataRecordManager.getFieldsForChildrenOfType(Collections.singletonList(sampleRecord), VeloxConstants.SAMPLE_CMO_INFO_RECORDS, user);
            if (sampleInfoExists(sampleInfos)) {
                Map<String, Object> sampleInfo = sampleInfos.get(0).get(0);
                return cmoSampleInfoConverter.convert(sampleInfo);

            }
        } catch (Exception e) {
            LoggerAndPopupDisplayer.logInfo(String.format("No cmo sample info for sample: %s", igoId));
        }

        return new CmoSampleInfo();
    }

    private boolean sampleInfoExists(List<List<Map<String, Object>>> sampleInfos) {
        return sampleInfos.size() > 0 && sampleInfos.get(0).size() > 0;
    }

}
