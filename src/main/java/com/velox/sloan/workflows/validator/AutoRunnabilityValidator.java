package com.velox.sloan.workflows.validator;

import com.velox.sloan.workflows.notificator.BulkNotificator;
import org.mskcc.domain.Request;

public class AutoRunnabilityValidator implements Validator {
    private BulkNotificator notificator;

    public AutoRunnabilityValidator(BulkNotificator notificator) {
        this.notificator = notificator;
    }

    @Override
    public boolean isValid(Request request) {
        return request.isBicAutorunnable();
    }

    @Override
    public BulkNotificator getBulkNotificator() {
        return notificator;
    }

    @Override
    public String getMessage(Request request) {
        return String.format("Request: %s is not BIC autorunnable", request.getId());
    }

    @Override
    public String getName() {
        return "Autorunnability validator";
    }

    @Override
    public boolean shouldValidate(Request request) {
        return true;
    }
}
