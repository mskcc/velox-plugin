package com.velox.sloan.workflows.config;

import com.velox.api.datarecord.DataRecord;
import com.velox.api.datarecord.DataRecordManager;
import com.velox.api.user.User;
import org.mskcc.util.VeloxConstants;

import java.util.ArrayList;
import java.util.List;

public class LimsConfigurationSource implements ConfigurationSource {
    private com.velox.api.datarecord.DataRecordManager dataRecordManager;
    private User user;

    public LimsConfigurationSource(DataRecordManager dataRecordManager, User user) {
        this.dataRecordManager = dataRecordManager;
        this.user = user;
    }

    @Override
    public List<String> getNotificationEmailAddresses() throws Exception {
        List<String> emailAddresses = new ArrayList<>();
        List<DataRecord> validatorConfigs = dataRecordManager.queryDataRecords(VeloxConstants.VALIDATOR_CONFIGURATION, "1=1", user);

        for (DataRecord validatorConfig : validatorConfigs) {
            emailAddresses.add(validatorConfig.getStringVal(VeloxConstants.NOTIFICATION_EMAIL_ADDRESS, user));
        }

        return emailAddresses;
    }
}
