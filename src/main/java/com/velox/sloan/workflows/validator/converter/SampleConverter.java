package com.velox.sloan.workflows.validator.converter;

import com.velox.api.datarecord.DataRecord;
import com.velox.api.user.User;
import com.velox.sloan.cmo.staticstrings.datatypes.DT_Sample;
import org.mskcc.domain.Sample;

public class SampleConverter implements Converter<DataRecord, Sample> {
    private final User user;

    public SampleConverter(User user) {
        this.user = user;
    }

    @Override
    public Sample convert(DataRecord sampleRecord) {
        String igoId = "";
        try {
            igoId = sampleRecord.getStringVal(DT_Sample.SAMPLE_ID, user);
            Sample sample = new Sample(igoId);
            sample.setRequestId(sampleRecord.getStringVal(DT_Sample.REQUEST_ID, user));

            return sample;
        } catch (Exception e) {
            throw new SampleConvertionException(String.format("Unable to convert sample data record to sample ", igoId), e);
        }
    }

}
