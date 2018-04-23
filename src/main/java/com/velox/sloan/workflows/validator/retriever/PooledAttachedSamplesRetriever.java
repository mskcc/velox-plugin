package com.velox.sloan.workflows.validator.retriever;

import com.velox.api.datarecord.DataRecord;
import com.velox.api.datarecord.NotFound;
import com.velox.api.user.User;
import com.velox.api.workflow.ActiveTask;
import org.apache.commons.lang3.StringUtils;
import org.mskcc.domain.sample.Sample;
import org.mskcc.util.Constants;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import static com.velox.sloan.workflows.LoggerAndPopupDisplayer.logInfo;

public class PooledAttachedSamplesRetriever implements AttachedSamplesRetriever {
    private com.velox.api.workflow.ActiveTask activeTask;
    private User user;

    public PooledAttachedSamplesRetriever(ActiveTask activeTask, User user) {
        this.activeTask = activeTask;
        this.user = user;
    }

    @Override
    public List<DataRecord> retrieve() throws Exception {
        List<DataRecord> attachedSamples = new ArrayList<>();

        List<DataRecord> attachedPools = getAttachedPools();
        logFoundPools(attachedPools);

        for (DataRecord attachedPool : attachedPools) {
            List<DataRecord> parentSamples = getNonPoolParentSamples(attachedPool);
            logSamplesFromPool(parentSamples, attachedPool);
            attachedSamples.addAll(parentSamples);
        }

        return attachedSamples;
    }

    private List<DataRecord> getNonPoolParentSamples(DataRecord attachedPool) throws Exception {
        List<DataRecord> parents = attachedPool.getParentsOfType(Sample.DATA_TYPE_NAME, user);

        List<DataRecord> parentSamples = new ArrayList<>();

        for (DataRecord parent : parents) {
            if (!isPool(parent))
                parentSamples.add(parent);
        }

        return parentSamples;
    }

    private List<DataRecord> getAttachedPools() throws Exception {
        List<DataRecord> attachedSamples = activeTask.getAttachedDataRecords(Sample.DATA_TYPE_NAME, user);

        List<DataRecord> attachedPools = new ArrayList<>();
        for (DataRecord attachedSample : attachedSamples) {
            if (isPool(attachedSample))
                attachedPools.add(attachedSample);
        }

        return attachedPools;
    }

    private boolean isPool(DataRecord attachedSample) throws NotFound, RemoteException {
        return attachedSample.getStringVal(Sample.SAMPLE_ID, user).startsWith(Constants.POOL_PREFIX);
    }

    private void logFoundPools(List<DataRecord> attachedPools) throws Exception {
        List<String> poolIds = getSampleIds(attachedPools);

        logInfo(String.format("Attached pools found: [%s]", StringUtils.join(poolIds, ",")));
    }

    private void logSamplesFromPool(List<DataRecord> parentSamples, DataRecord attachedPool) throws NotFound, RemoteException {
        List<String> sampleIds = getSampleIds(parentSamples);
        String poolId = attachedPool.getStringVal(Sample.SAMPLE_ID, user);

        logInfo(String.format("Pool: %s has samples: [%s]", poolId, StringUtils.join(sampleIds, ",")));
    }

    private List<String> getSampleIds(List<DataRecord> samples) throws NotFound, RemoteException {
        List<String> sampleIds = new ArrayList<>();

        for (DataRecord sample : samples) {
            String sampleId = sample.getStringVal(Sample.SAMPLE_ID, user);
            sampleIds.add(sampleId);
        }
        return sampleIds;
    }
}
