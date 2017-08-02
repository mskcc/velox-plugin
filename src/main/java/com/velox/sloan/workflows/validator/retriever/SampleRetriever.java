package com.velox.sloan.workflows.validator.retriever;

import org.mskcc.domain.sample.CmoSampleInfo;

public interface SampleRetriever {
    CmoSampleInfo getCmoSampleInfo(String igoId);
}
