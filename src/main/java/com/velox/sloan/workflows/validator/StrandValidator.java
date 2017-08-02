package com.velox.sloan.workflows.validator;

import com.velox.sloan.workflows.notificator.BulkNotificator;
import org.mskcc.domain.Recipe;
import org.mskcc.domain.Request;
import org.mskcc.domain.RequestType;
import org.mskcc.domain.Strand;

public class StrandValidator implements Validator {
    private BulkNotificator notificator;

    public StrandValidator(BulkNotificator notificator) {
        this.notificator = notificator;
    }

    @Override
    public boolean isValid(Request request) {
        return getNumberOfNonEmptyStrands(request) > 0;
    }

    @Override
    public BulkNotificator getBulkNotificator() {
        return notificator;
    }

    private long getNumberOfNonEmptyStrands(Request request) {
        return request.getStrands().stream().filter(s -> s != Strand.EMPTY).count();
    }

    @Override
    public String getMessage(Request request) {
        return String.format("Request %s has no strand information", request.getId());
    }

    @Override
    public String getName() {
        return "Strand validator for RNASeq requests";
    }

    @Override
    public boolean shouldValidate(Request request) {
        return isRNASeqRequest(request);
    }

    private boolean isRNASeqRequest(Request request) {
        return request.getRequestType() == RequestType.RNASEQ
                || (request.getSamplesRecipes().size() == 1 && request.getSamplesRecipes().get(0) == Recipe.SMARTER_AMP_SEQ);
    }
}
