package com.velox.sloan.workflows.validator;

import com.velox.sloan.workflows.notificator.Notificator;
import org.mskcc.domain.Request;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

class RequestValidator {
    private final List<Validator> validators = new LinkedList<>();

    public boolean isValid(Request request) {
        LoggerAndPopup.logDebug(String.format("Validating request: %s", request.getId()));
        boolean validForAllValidators = true;
        for (Validator validator : validators) {
            if (validator.shouldValidate(request)) {
                LoggerAndPopup.logInfo("Validating using validator: " + validator.getName());
                boolean valid = validator.isValid(request);
                if (!valid) {
                    validator.addMessage(request);
                    validForAllValidators = false;
                }
                LoggerAndPopup.logInfo("Validation result: " + (valid ? "valid" : "invalid"));
            }
        }

        LoggerAndPopup.logInfo(String.format("Request: %s is %s", request.getId(), validForAllValidators ? "valid" : "invalid"));

        if (!validForAllValidators)
            getAllErrorNotificators().forEach(en -> en.notifyAllMessages(request.getId()));

        return validForAllValidators;
    }

    void addValidator(Validator validator) {
        validators.add(validator);
    }

    private Set<Notificator> getAllErrorNotificators() {
        return validators.stream().map(v -> v.getNotificator()).collect(Collectors.toSet());
    }
}
