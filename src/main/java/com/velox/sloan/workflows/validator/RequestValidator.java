package com.velox.sloan.workflows.validator;

import com.velox.sloan.workflows.LoggerAndPopupDisplayer;
import com.velox.sloan.workflows.notificator.BulkNotificator;
import org.mskcc.domain.Request;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

class RequestValidator {
    private final List<Validator> validators = new LinkedList<>();

    public boolean isValid(Request request) {
        boolean isRequestValid = true;
        for (Validator validator : validators) {
            if (!isValid(request, validator))
                isRequestValid = false;
        }

        notifyErrorNotificators(request);

        return isRequestValid;
    }

    private boolean isValid(Request request, Validator validator) {
        if (!validator.shouldValidate(request))
            return true;

        boolean valid = validator.isValid(request);
        if (!valid)
            validator.addMessage(request);

        LoggerAndPopupDisplayer.logInfo(String.format("Validation result for request: %s using validator: %s, result: %s",
                request.getId(), validator.getName(), valid ? "valid" : "invalid"));

        return valid;
    }

    private void notifyErrorNotificators(Request request) {
        getNofiticatorsToNotify()
                .forEach(en -> en.notifyAllMessages(request.getId()));
    }

    private Stream<BulkNotificator> getNofiticatorsToNotify() {
        return validators.stream()
                .map(v -> v.getBulkNotificator())
                .distinct()
                .filter(n -> !n.getMessages().isEmpty());
    }

    void addValidator(Validator validator) {
        validators.add(validator);
    }

}
