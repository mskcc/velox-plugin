package com.velox.sloan.workflows.validator.converter;

import com.velox.api.datarecord.DataRecord;
import com.velox.api.user.User;
import org.mskcc.domain.Request;
import org.mskcc.util.VeloxConstants;

public class RequestConverter implements Converter<DataRecord, Request> {
    private User user;

    public RequestConverter(User user) {
        this.user = user;
    }

    @Override
    public Request convert(DataRecord requestRecord) {
        String requestId = "";
        try {
            requestId = requestRecord.getStringVal(VeloxConstants.REQUEST_ID, user);
            boolean isAutorunnable = requestRecord.getBooleanVal(VeloxConstants.BIC_AUTORUNNABLE, user);

            Request request = new Request(requestId);
            request.setBicAutorunnable(isAutorunnable);
            return request;
        } catch (Exception e) {
            throw new RuntimeException(String.format("Unable to create request object %s", requestId), e);
        }
    }
}
