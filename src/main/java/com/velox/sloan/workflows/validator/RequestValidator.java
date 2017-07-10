package com.velox.sloan.workflows.validator;

import com.velox.sloan.workflows.LoggerAndPopupDisplayer;
import com.velox.sloan.workflows.notificator.Notificator;
import org.mskcc.domain.Request;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

class RequestValidator {
    private final List<Validator> validators = new LinkedList<>();

    public boolean isValid(Request request) {
        for (Validator validator : validators) {
            if (validator.shouldValidate(request)) {
                validate(request, validator);
            }
        }

        notifyErrorNotificators(request);

        return getNofiticatorsToNotify().count() == 0;
    }

    private void validate(Request request, Validator validator) {
        boolean valid = validator.isValid(request);
        if (!valid)
            validator.addMessage(request);

        LoggerAndPopupDisplayer.logInfo(String.format("Validation result for request: %s using validator: %s, result: %s",
                request.getId(), validator.getName(), valid ? "valid" : "invalid"));
    }

    private void notifyErrorNotificators(Request request) {
        getNofiticatorsToNotify()
                .forEach(en -> en.notifyAllMessages(request.getId()));
    }

    private Stream<Notificator> getNofiticatorsToNotify() {
        return validators.stream()
                .map(v -> v.getNotificator())
                .distinct()
                .filter(n -> !n.getMessages().isEmpty());
    }

    void addValidator(Validator validator) {
        validators.add(validator);
    }

}
