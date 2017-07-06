package com.velox.sloan.workflows.validator;

import com.velox.api.datarecord.DataRecord;
import com.velox.api.plugin.PluginResult;
import com.velox.api.user.User;
import com.velox.api.util.ServerException;
import com.velox.api.workflow.ActiveTask;
import com.velox.sapioutils.server.plugin.DefaultGenericPlugin;
import com.velox.sapioutils.shared.enums.PluginOrder;
import com.velox.sloan.cmo.staticstrings.datatypes.DT_Request;
import com.velox.sloan.cmo.staticstrings.datatypes.DT_Sample;
import com.velox.sloan.cmo.utilities.SloanCMOUtils;
import com.velox.sloan.workflows.notificator.MessageDisplay;
import com.velox.sloan.workflows.notificator.NotificatorFactory;
import com.velox.sloan.workflows.validator.converter.Converter;
import com.velox.sloan.workflows.validator.converter.SampleConverter;
import com.velox.sloan.workflows.validator.converter.SamplesToRequestsConverter;
import org.mskcc.domain.Request;
import org.mskcc.domain.Sample;

import java.util.*;
import java.util.stream.Collectors;

public class RequestValidatorPlugin extends DefaultGenericPlugin implements MessageDisplay {
    private final RequestValidator requestValidator = new RequestValidator();
    private NotificatorFactory notificatorFactory;
    private Map<String, DataRecord> requestIdToRecord = new HashMap<>();
    private Map<String, DataRecord> sampleIgoIdToRecord = new HashMap<>();
    private Map<String, Request> requestIdToRequest = new HashMap<>();

    public RequestValidatorPlugin() {
        setTaskSubmit(true);
        setOrder(PluginOrder.LATE.getOrder() - 5);
    }

    @Override
    protected boolean shouldRun() throws Throwable {
        return new SloanCMOUtils(managerContext).shouldRunPlugin();
    }

    @Override
    protected PluginResult run() throws Throwable {
        try {
            LoggerAndPopup.configure(this);
            logInfo("Running validator plugin log info");
            init();
            validateRequests();
        } catch (Exception e) {
            logError(String.format("Unable to validate recipes for workflow: %s, task: %s", activeWorkflow.getActiveWorkflowName(), activeTask.getFullName()), e);
        }

        return new PluginResult(true);
    }

    private void validateRequests() {
        for (Request request : requestIdToRequest.values())
            requestValidator.isValid(request);
    }

    private void init() throws Exception {
        notificatorFactory = new NotificatorFactory(this);
        sampleIgoIdToRecord = getSampleIgoIdToRecordMap();
        requestIdToRequest = getRequestIdToRequestMap();
        initValidators();
    }

    private void initValidators() throws Exception {
        for (Validator validator : getValidators()) {
            requestIdToRequest = validator.updateRequests(requestIdToRequest);
            requestValidator.addValidator(validator);
        }
    }

    private List<Validator> getValidators() {
        return Arrays.asList(
                new RecipeValidator(notificatorFactory.getPopupNotificator(), user, sampleIgoIdToRecord),
                new AutoRunnabilityValidator(notificatorFactory.getEmailNotificator(), user, requestIdToRecord),
                new SpeciesValidator(notificatorFactory.getEmailNotificator(), user, sampleIgoIdToRecord),
                new XenograftSpeciesValidator(notificatorFactory.getEmailNotificator(), user, sampleIgoIdToRecord),
                new StrandValidator(notificatorFactory.getEmailNotificator(), user, dataRecordManager, sampleIgoIdToRecord)
        );
    }

    private Map<String, DataRecord> getSampleIgoIdToRecordMap() throws Exception {
        Map<String, DataRecord> sampleIgoIdTSampleRecord = new HashMap<>();
        List<DataRecord> sampleRecords = activeTask.getAttachedDataRecords(DT_Sample.DATA_TYPE, user);
        for (DataRecord sampleRecord : sampleRecords) {
            String igoId = sampleRecord.getStringVal(DT_Sample.SAMPLE_ID, user);
            sampleIgoIdTSampleRecord.put(igoId, sampleRecord);
        }

        return sampleIgoIdTSampleRecord;
    }

    private Map<String, Request> getRequestIdToRequestMap() throws Exception {
        requestIdToRecord = getRequestIdToRecordMap();

        Converter<DataRecord, Sample> converter = new SampleConverter(user);
        List<Sample> samples = sampleIgoIdToRecord.values().stream()
                .map(s -> converter.convert(s))
                .collect(Collectors.toList());

        SamplesToRequestsConverter samplesToRequestsConverter = new SamplesToRequestsConverter();

        return samplesToRequestsConverter.convert(samples);
    }

    private Map<String, DataRecord> getRequestIdToRecordMap() throws Exception {
        SloanCMOUtils sloanCMOUtils = new SloanCMOUtils(managerContext);
        List<DataRecord> sampleRecords = activeTask.getAttachedDataRecords(DT_Sample.DATA_TYPE, user);
        Set<DataRecord> requestRecords = sloanCMOUtils.getAssociatedRequestRecordList(sampleRecords).stream()
                .flatMap(r -> r.stream())
                .collect(Collectors.toSet());

        Map<String, DataRecord> requestIdToRequest = new HashMap<>();
        for (DataRecord requestRecord : requestRecords) {
            String requestId = requestRecord.getStringVal(DT_Request.REQUEST_ID, user);
            requestIdToRequest.put(requestId, requestRecord);
        }
        return requestIdToRequest;
    }

    @Override
    public void logErrorMessage(String message) {
        logError(message);
    }

    @Override
    public void logDebugMessage(String msg) {
        logDebug(msg);
    }

    @Override
    public void logInfoMessage(String msg) {
        logInfo(msg);
    }

    @Override
    public void displayWarningPopup(String message) {
        try {
            displayWarning(message);
        } catch (ServerException e) {
            logError("Unable to display warning popup: " + message);
        }
    }

    User getUser() {
        return user;
    }

    ActiveTask getActiveTask() {
        return activeTask;
    }
}
