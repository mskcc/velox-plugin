package com.velox.sloan.workflows.validator.converter;

import org.mskcc.domain.sample.CmoSampleInfo;
import org.mskcc.util.VeloxConstants;

import java.util.Map;

public class CmoSampleInfoConverter implements Converter<Map<String, Object>, CmoSampleInfo> {
    @Override
    public CmoSampleInfo convert(Map<String, Object> sampleInfo) {
        CmoSampleInfo cmoSampleInfo = new CmoSampleInfo();
        cmoSampleInfo.setCMOSampleClass(String.valueOf(sampleInfo.get(VeloxConstants.CMO_SAMPLE_CLASS)));
        cmoSampleInfo.setTumorOrNormal(String.valueOf(sampleInfo.get(VeloxConstants.TUMOR_OR_NORMAL)));
        return cmoSampleInfo;
    }
}
