package com.velox.sloan.workflows.validator;

import com.velox.api.datarecord.DataRecord;
import com.velox.api.plugin.PluginResult;
import com.velox.api.user.User;
import com.velox.api.util.ServerException;
import com.velox.api.workflow.ActiveTask;
import com.velox.sapioutils.server.plugin.DefaultGenericPlugin;
import com.velox.sapioutils.shared.enums.PluginOrder;
import com.velox.sloan.cmo.staticstrings.datatypes.DT_Sample;
import com.velox.sloan.cmo.utilities.SloanCMOUtils;
import com.velox.sloan.workflows.LoggerAndPopupDisplayer;
import com.velox.sloan.workflows.config.AppConfig;
import com.velox.sloan.workflows.config.LimsConfigurationSource;
import com.velox.sloan.workflows.notificator.MessageDisplay;
import com.velox.sloan.workflows.notificator.NotificatorFactory;
import com.velox.sloan.workflows.validator.converter.*;
import com.velox.sloan.workflows.validator.retriever.SampleRetriever;
import com.velox.sloan.workflows.validator.retriever.VeloxRequestRetriever;
import org.mskcc.domain.Request;
import org.mskcc.domain.sample.Sample;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class RequestValidatorPlugin extends DefaultGenericPlugin implements MessageDisplay {
    private final RequestValidator requestValidator = new RequestValidator();
    private final CmoSampleInfoConverter cmoSampleInfoConverter = new CmoSampleInfoConverter();
    private NotificatorFactory notificatorFactory;
    private Map<String, DataRecord> sampleIgoIdToRecord = new HashMap<>();
    private Map<String, Request> requestIdToRequest = new HashMap<>();
    private VeloxRequestRetriever requestRetriever;
    private RequestConverter requestConverter;
    private Predicate<Request> rnaSeqRequestPredicate = new RnaSeqRequestPredicate();

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
            LoggerAndPopupDisplayer.configure(this);
            logInfo("Running validator plugin");
            init();
            validateRequests();
        } catch (Exception e) {
            logError(String.format("Unable to validate requests for workflow: %s, task: %s", activeWorkflow.getActiveWorkflowName(), activeTask.getFullName()), e);
        }

        return new PluginResult(true);
    }

    private void validateRequests() {
        for (Request request : requestIdToRequest.values())
            requestValidator.isValid(request);
    }

    private void init() throws Exception {
        loadProperties();
        requestConverter = new RequestConverter(user);
        requestRetriever = new VeloxRequestRetriever(dataRecordManager, user);
        notificatorFactory = new NotificatorFactory(this);
        sampleIgoIdToRecord = getSampleIgoIdToRecordMap();
        requestIdToRequest = getRequestIdToRequestMap();
        initValidators();
    }

    private void loadProperties() {
        AppConfig appConfig = new AppConfig();
        appConfig.configure(new LimsConfigurationSource(dataRecordManager, user));
    }

    private void initValidators() throws Exception {
        for (Validator validator : getValidators()) {
            requestValidator.addValidator(validator);
        }
    }

    private List<Validator> getValidators() {
        SamplesValidator samplesValidator = new AllMatchSamplesValidator();
        return Arrays.asList(
                new RecipeValidator(notificatorFactory.getPopupNotificator()),
                new AutoRunnabilityValidator(notificatorFactory.getEmailNotificator()),
                new SpeciesValidator(notificatorFactory.getEmailNotificator()),
                new XenograftSpeciesValidator(notificatorFactory.getEmailNotificator()),
                new StrandValidator(notificatorFactory.getEmailNotificator()),
                new SampleClassValidator(notificatorFactory.getEmailNotificator()),
                new KapaProtocolValidator(notificatorFactory.getEmailNotificator(), new KapaProtocolValidator.KapaProtocolValidPredicate(), samplesValidator),
                new NimleGenProtocolValidator(notificatorFactory.getEmailNotificator(), new NimbGenProtocolValidPredicate(), samplesValidator),
                new DnaLibraryProtocolValidator(notificatorFactory.getEmailNotificator(), new DnaLibraryProtocolValidator.DnaLibraryProtocolValidPredicate(), samplesValidator)
        );
    }

    private Map<String, DataRecord> getSampleIgoIdToRecordMap() throws Exception {
        Map<String, DataRecord> sampleIgoIdToSampleRecord = new HashMap<>();
        List<DataRecord> sampleRecords = activeTask.getAttachedDataRecords(DT_Sample.DATA_TYPE, user);
        for (DataRecord sampleRecord : sampleRecords) {
            String igoId = sampleRecord.getStringVal(DT_Sample.SAMPLE_ID, user);
            sampleIgoIdToSampleRecord.put(igoId, sampleRecord);
        }

        return sampleIgoIdToSampleRecord;
    }

    private Map<String, Request> getRequestIdToRequestMap() throws Exception {
        SampleRetriever sampleRetriever = new SampleRetriever(sampleIgoIdToRecord, dataRecordManager, user, cmoSampleInfoConverter);
        Converter<DataRecord, Sample> sampleConverter = new SampleConverter(user, dataRecordManager, notificatorFactory.getEmailNotificator(), sampleRetriever);
        SampleRecordsToRequestsConverter sampleRecordsToRequestsConverter = new SampleRecordsToRequestsConverter(sampleConverter, requestRetriever, requestConverter, rnaSeqRequestPredicate);

        return sampleRecordsToRequestsConverter.convert(sampleIgoIdToRecord.values());
    }

    @Override
    public void logErrorMessage(String message) {
        logError(message);
    }

    @Override
    public void logErrorMessage(String message, Throwable t) {
        logError(t, message);
    }

    @Override
    public void logDebugMessage(String msg) {
        logDebug(msg);
    }

    @Override
    public void logDebugMessage(String message, Throwable t) {
        logDebug(t, message);
    }

    @Override
    public void logInfoMessage(String msg) {
        logInfo(msg);
    }

    @Override
    public void logInfoMessage(String msg, Throwable t) {
        logInfo(t, msg);
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
