package com.velox.sloan.workflows.validator.retriever;

import com.velox.api.datarecord.DataRecord;

import java.util.List;

public interface AttachedSamplesRetriever {
    List<DataRecord> retrieve() throws Exception;
}
