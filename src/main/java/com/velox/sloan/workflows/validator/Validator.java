package com.velox.sloan.workflows.validator;

import com.velox.sloan.workflows.notificator.Notificator;
import org.mskcc.domain.Request;

import java.util.Map;

public abstract class Validator {
    private Notificator notificator;

    public Validator(Notificator notificator) {
        this.notificator = notificator;
    }

    abstract boolean isValid(Request request);

    Notificator getNotificator() {
        return notificator;
    }

    abstract String getMessage(Request request);

    abstract String getName();

    abstract boolean shouldValidate(Request request);

    void addMessage(Request request) {
        getNotificator().addMessage(request.getId(), getMessage(request));
    }
}
