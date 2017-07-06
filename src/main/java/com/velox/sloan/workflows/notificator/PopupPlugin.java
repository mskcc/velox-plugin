package com.velox.sloan.workflows.notificator;

import com.velox.api.plugin.PluginResult;
import com.velox.api.util.ServerException;
import com.velox.sapioutils.server.plugin.DefaultGenericPlugin;
import com.velox.sapioutils.shared.enums.PluginOrder;

public class PopupPlugin extends DefaultGenericPlugin {
    public PopupPlugin() {
        setTaskSubmit(true);
        setOrder(PluginOrder.LATE.getOrder() - 5);
    }

    @Override
    protected boolean shouldRun() throws Throwable {
        return true;
    }

    @Override
    protected PluginResult run() throws Throwable {
        return super.run();
    }

    public void showWarningPopup(String message) {
        try {
            displayWarning(message);
        } catch (ServerException e) {
            logError(String.format("Unable to display warning: %s", message));
        }
    }

    public void logDebugMessge(String message) {
        logDebug(message);
    }
}
