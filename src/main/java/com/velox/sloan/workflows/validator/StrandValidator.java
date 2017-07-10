package com.velox.sloan.workflows.validator;

import com.velox.sloan.workflows.notificator.Notificator;
import org.mskcc.domain.Recipe;
import org.mskcc.domain.Request;
import org.mskcc.domain.Strand;
import org.mskcc.util.Constants;

import java.util.Objects;

public class StrandValidator extends Validator {
    public StrandValidator(Notificator notificator) {
        super(notificator);
    }

    @Override
    boolean isValid(Request request) {
        return getNumberOfNonEmptyStrands(request) > 0;
    }

    private long getNumberOfNonEmptyStrands(Request request) {
        return request.getStrands().stream().filter(s -> s != Strand.EMPTY).count();
    }

    @Override
    String getMessage(Request request) {
        return String.format("Request %s has no strand information", request.getId());
    }

    @Override
    String getName() {
        return "Strand validator for RNASeq requests";
    }

    @Override
    boolean shouldValidate(Request request) {
        return isRNASeqRequest(request);
    }

    private boolean isRNASeqRequest(Request request) {
        return Objects.equals(request.getRequestType(), Constants.RNASEQ)
                || (request.getSamplesRecipes().size() == 1 && request.getSamplesRecipes().get(0) == Recipe.SMARTER_AMP_SEQ);
    }
}
