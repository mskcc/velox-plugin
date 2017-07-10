package com.velox.sloan.workflows.validator;

import com.velox.sloan.workflows.notificator.Notificator;
import org.junit.Before;
import org.junit.Test;
import org.mskcc.domain.Recipe;
import org.mskcc.domain.Request;
import org.mskcc.domain.Strand;
import org.mskcc.domain.sample.Sample;
import org.mskcc.util.Constants;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class StrandValidatorTest {
    private StrandValidator strandValidator;

    @Before
    public void setUp() {
        strandValidator = new StrandValidator(
                mock(Notificator.class));
    }

    @Test
    public void whenRequestIsRNASeq_shouldRunValidator() {
        Request rnaseqRequest = getRnaSeqRequestType();
        Request rnaseqRequestSmarterAmpRecipe = getSmarterAmpRecipeRequest();

        assertTrue(strandValidator.shouldValidate(rnaseqRequest));
        assertTrue(strandValidator.shouldValidate(rnaseqRequestSmarterAmpRecipe));
    }

    private Request getSmarterAmpRecipeRequest() {
        Request request = new Request("12345_P");
        Sample smarterAmpSample = new Sample("12345_P_1");
        smarterAmpSample.setRecipe(Recipe.SMARTER_AMP_SEQ);
        request.putSampleIfAbsent(smarterAmpSample);
        return request;
    }

    @Test
    public void whenRequestIsNonRNASeq_shouldNotRunValidator() {
        Request nonRnaseqRequest = getNonRnaSeqRequest();

        boolean shouldValidate = strandValidator.shouldValidate(nonRnaseqRequest);

        assertFalse(shouldValidate);
    }

    @Test
    public void whenRequestHasStrandSet_shouldRequestBeValid() {
        Request validRequestWithStrandSet = getValidRequestWithStrandSet();

        boolean valid = strandValidator.isValid(validRequestWithStrandSet);

        assertTrue(valid);
    }

    @Test
    public void whenRequestHasNoStrandSet_shouldRequestBeInvalid() {
        Request invalidRequestNoStrandSet = getInvalidRequestNoStrandSet();

        boolean valid = strandValidator.isValid(invalidRequestNoStrandSet);

        assertFalse(valid);
    }

    private Request getValidRequestWithStrandSet() {
        Request request = new Request("12345_P");
        request.addStrand(Strand.REVERSE);
        return request;
    }

    private Request getInvalidRequestNoStrandSet() {
        return new Request("12345_P");
    }

    private Request getRnaSeqRequestType() {
        Request request = new Request("12345_P");
        request.setRequestType(Constants.RNASEQ);
        return request;
    }

    private Request getNonRnaSeqRequest() {
        return new Request("12345_P");
    }

}