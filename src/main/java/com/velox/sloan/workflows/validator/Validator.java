package com.velox.sloan.workflows.validator;

import com.velox.sloan.workflows.notificator.BulkNotificator;
import org.mskcc.domain.Request;

public interface Validator {
    boolean isValid(Request request);

    BulkNotificator getBulkNotificator();

    String getMessage(Request request);

    String getName();

    boolean shouldValidate(Request request);

    default void addMessage(Request request) {
        getBulkNotificator().addMessage(request.getId(), getMessage(request));
    }
}
