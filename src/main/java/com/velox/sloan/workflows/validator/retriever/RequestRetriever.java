package com.velox.sloan.workflows.validator.retriever;

import com.velox.api.datarecord.DataRecord;

public interface RequestRetriever {
    DataRecord retrieve(String reqId) throws Exception;
}
