package com.velox.sloan.workflows.validator;

import com.velox.sloan.workflows.notificator.Notificator;
import org.mskcc.domain.Request;

public class AutoRunnabilityValidator extends Validator {

    public AutoRunnabilityValidator(Notificator notificator) {
        super(notificator);
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
    public boolean shouldValidate(Request request) {
        return true;
    }
}
