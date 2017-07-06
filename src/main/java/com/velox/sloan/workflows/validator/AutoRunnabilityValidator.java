package com.velox.sloan.workflows.validator;

import com.velox.api.datarecord.DataRecord;
import com.velox.api.user.User;
import com.velox.sloan.workflows.notificator.Notificator;
import org.mskcc.domain.Request;

import java.util.Map;

public class AutoRunnabilityValidator extends Validator {
    private final User user;
    private final Map<String, DataRecord> requestIdToRecord;

    public AutoRunnabilityValidator(Notificator notificator, User user, Map<String, DataRecord> requestIdToRecord) {
        super(notificator);
        this.user = user;
        this.requestIdToRecord = requestIdToRecord;
    }

    @Override
    public boolean isValid(Request request) {
        return request.isBicAutorunnable();
    }

    @Override
    public String getMessage(Request request) {
        return String.format("Request: %s is not autorunnable", request.getId());
    }

    @Override
    public String getName() {
        return "Autorunnability validator";
    }

    @Override
    public Map<String, Request> updateRequests(Map<String, Request> requests) throws Exception {
        for (Request request : requests.values()) {
            DataRecord dataRecord = requestIdToRecord.get(request.getId());
            if(dataRecord != null) {
                boolean autorunnable = dataRecord.getBooleanVal("BicAutorunnable", user);
                request.setBicAutorunnable(autorunnable);
            }
        }

        return requests;
    }

    @Override
    public boolean shouldValidate(Request request) {
        return true;
    }
}
