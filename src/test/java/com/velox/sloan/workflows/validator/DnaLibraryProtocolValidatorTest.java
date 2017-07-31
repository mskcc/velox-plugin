package com.velox.sloan.workflows.validator;

import com.velox.sloan.workflows.notificator.BulkNotificator;
import org.junit.Before;
import org.junit.Test;
import org.mskcc.domain.Request;
import org.mskcc.domain.RequestType;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.mock;

public class DnaLibraryProtocolValidatorTest {
    private DnaLibraryProtocolValidator dnaLibraryProtocolValidator;
    private BulkNotificator nofificatiorMock = mock(BulkNotificator.class);
    private Request request;

    @Before
    public void setUp() throws Exception {
        dnaLibraryProtocolValidator = new DnaLibraryProtocolValidator(nofificatiorMock, s -> true, mock(SamplesValidator.class));
        request = new Request("12345_P");
    }

    @Test
    public void whenRequestIsNotImpactOrExome_shouldNotRunValidator() {
        request.setRequestType(RequestType.RNASEQ);

        assertThat(dnaLibraryProtocolValidator.shouldValidate(request), is(false));
    }

    @Test
    public void whenRequestIsImpact_shouldRunValidator() {
        request.setRequestType(RequestType.IMPACT);

        assertThat(dnaLibraryProtocolValidator.shouldValidate(request), is(true));
    }

    @Test
    public void whenRequestIsExome_shouldRunValidator() {
        request.setRequestType(RequestType.EXOME);

        assertThat(dnaLibraryProtocolValidator.shouldValidate(request), is(true));
    }
}