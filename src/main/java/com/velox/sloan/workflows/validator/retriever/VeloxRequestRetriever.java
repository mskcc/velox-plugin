package com.velox.sloan.workflows.validator.retriever;

import com.velox.api.datarecord.DataRecord;
import com.velox.api.datarecord.DataRecordManager;
import com.velox.api.user.User;
import org.mskcc.util.VeloxConstants;

import java.util.List;

public class VeloxRequestRetriever implements RequestRetriever {
    private DataRecordManager dataRecordManager;
    private User user;

    public VeloxRequestRetriever(DataRecordManager dataRecordManager, User user) {
        this.dataRecordManager = dataRecordManager;
        this.user = user;
    }

    @Override
    public DataRecord retrieve(String reqId) throws Exception {
        List<DataRecord> requestRecords = dataRecordManager.queryDataRecords(VeloxConstants.REQUEST, "RequestId = '" + reqId + "'", user);

        if(requestRecords == null || requestRecords.size() == 0)
            throw new RuntimeException(String.format("Request: %s doesn't exist", reqId));

        return requestRecords.get(0);
    }
}
