package com.velox.sloan.workflows.validator;

import com.velox.sloan.workflows.notificator.BulkNotificator;
import org.junit.Before;
import org.junit.Test;
import org.mskcc.domain.Request;
import org.mskcc.domain.RequestType;

import java.util.function.Predicate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.mock;

public class NimleGenProtocolValidatorTest {
    private NimleGenProtocolValidator nimleGenProtocolValidator;

    @Before
    public void setUp() throws Exception {
        nimleGenProtocolValidator = new NimleGenProtocolValidator(mock(BulkNotificator.class), mock(Predicate.class), mock(SamplesValidator.class));
    }

    @Test
    public void whenRequestIsImpact_shouldValidate() {
        Request request = new Request("432");
        request.setRequestType(RequestType.IMPACT);

        boolean shouldValidate = nimleGenProtocolValidator.shouldValidate(request);

        assertThat(shouldValidate, is(true));
    }

    @Test
    public void whenRequestIsExome_shouldNotValidate() {
        Request request = new Request("432");
        request.setRequestType(RequestType.EXOME);

        boolean shouldValidate = nimleGenProtocolValidator.shouldValidate(request);

        assertThat(shouldValidate, is(false));
    }

    @Test
    public void whenRequestIsRNASeq_shouldNotValidate() {
        Request request = new Request("432");
        request.setRequestType(RequestType.RNASEQ);

        boolean shouldValidate = nimleGenProtocolValidator.shouldValidate(request);

        assertThat(shouldValidate, is(false));
    }

    @Test
    public void whenRequestIsOther_shouldNotValidate() {
        Request request = new Request("432");
        request.setRequestType(RequestType.OTHER);

        boolean shouldValidate = nimleGenProtocolValidator.shouldValidate(request);

        assertThat(shouldValidate, is(false));
    }

}