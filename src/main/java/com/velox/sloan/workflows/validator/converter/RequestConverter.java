package com.velox.sloan.workflows.validator.converter;

import com.velox.api.datarecord.DataRecord;
import com.velox.api.datarecord.NotFound;
import com.velox.api.user.User;
import org.mskcc.domain.Request;
import org.mskcc.domain.RequestType;
import org.mskcc.util.VeloxConstants;

import java.rmi.RemoteException;

public class RequestConverter implements Converter<DataRecord, Request> {
    private final ExomeRequestPredicate exomeRequestPredicate = new ExomeRequestPredicate();
    private final ImpactRequestPredicate impactRequestPredicate = new ImpactRequestPredicate();
    private final User user;

    public RequestConverter(User user) {
        this.user = user;
    }

    @Override
    public Request convert(DataRecord requestRecord) {
        String requestId = "";
        try {
            requestId = requestRecord.getStringVal(VeloxConstants.REQUEST_ID, user);
            Request request = new Request(requestId);
            request.setBicAutorunnable(isAutorunnable(requestRecord));
            request.setName(requestRecord.getPickListVal(VeloxConstants.REQUEST_NAME, user));
            request.setRequestType(getRequestType(request));

            return request;
        } catch (Exception e) {
            throw new RuntimeException(String.format("Unable to create request object %s", requestId), e);
        }
    }

    private boolean isAutorunnable(DataRecord requestRecord) throws NotFound, RemoteException {
        return requestRecord.getBooleanVal(VeloxConstants.BIC_AUTORUNNABLE, user);
    }

    private RequestType getRequestType(Request request) {
        if (isExome(request))
            return RequestType.EXOME;
        if (isImpact(request))
            return RequestType.IMPACT;
        return RequestType.OTHER;
    }

    private boolean isExome(Request request) {
        return exomeRequestPredicate.test(request);
    }

    private boolean isImpact(Request request) {
        return impactRequestPredicate.test(request);
    }


}
