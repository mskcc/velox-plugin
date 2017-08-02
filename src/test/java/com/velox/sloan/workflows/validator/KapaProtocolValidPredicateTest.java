package com.velox.sloan.workflows.validator;

import com.velox.sloan.workflows.notificator.BulkNotificator;
import org.junit.Before;
import org.junit.Test;
import org.mskcc.domain.Request;
import org.mskcc.domain.RequestType;
import org.mskcc.domain.sample.Sample;

import java.util.Set;
import java.util.function.Predicate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class KapaProtocolValidPredicateTest {
    private final SamplesValidator samplesValidator = new SamplesValidatorMock();
    private Request request;
    private int id;

    @Before
    public void setUp() throws Exception {
        request = new Request("12355_P");
    }

    @Test
    public void whenRequestIsImpact_shouldNotRunValidator() {
        KapaProtocolValidator validKapaProtocolValidator = getValidKapaValidator();
        request.setRequestType(RequestType.IMPACT);

        boolean shouldValidate = validKapaProtocolValidator.shouldValidate(request);

        assertThat(shouldValidate, is(false));
    }

    @Test
    public void whenRequestIsRnaSeq_shouldNotRunValidator() {
        KapaProtocolValidator validKapaProtocolValidator = getValidKapaValidator();
        request.setRequestType(RequestType.RNASEQ);

        boolean shouldValidate = validKapaProtocolValidator.shouldValidate(request);

        assertThat(shouldValidate, is(false));
    }

    @Test
    public void whenRequestIsOther_shouldNotRunValidator() {
        KapaProtocolValidator validKapaProtocolValidator = getValidKapaValidator();
        request.setRequestType(RequestType.OTHER);

        boolean shouldValidate = validKapaProtocolValidator.shouldValidate(request);

        assertThat(shouldValidate, is(false));
    }

    @Test
    public void whenRequestIsExome_shouldRunValidator() {
        KapaProtocolValidator validKapaProtocolValidator = getValidKapaValidator();
        request.setRequestType(RequestType.EXOME);

        boolean shouldValidate = validKapaProtocolValidator.shouldValidate(request);

        assertThat(shouldValidate, is(true));
    }

    @Test
    public void whenSamplesValidatorIsValid_shouldBeValid() {
        KapaProtocolValidator validKapaProtocolValidator = getValidKapaValidator();

        boolean valid = validKapaProtocolValidator.isValid(request);

        assertThat(valid, is(true));
    }

    @Test
    public void whenSamplesValidatorIsInvalid_shouldBeInvalid() {
        KapaProtocolValidator invalidKapaProtocolValidator = getInvalidKapaValidator();

        boolean valid = invalidKapaProtocolValidator.isValid(request);

        assertThat(valid, is(false));
    }

    private KapaProtocolValidator getValidKapaValidator() {
        SamplesValidator samplesValidatorMock = mock(SamplesValidator.class);
        when(samplesValidatorMock.isValid(any(), any())).thenReturn(true);
        return new KapaProtocolValidator(mock(BulkNotificator.class), new KapaProtocolValidPredicateMock(), samplesValidatorMock);
    }

    private KapaProtocolValidator getInvalidKapaValidator() {
        SamplesValidator samplesValidatorMock = mock(SamplesValidator.class);
        when(samplesValidatorMock.isValid(any(), any())).thenReturn(false);
        return new KapaProtocolValidator(mock(BulkNotificator.class), new KapaProtocolValidPredicateMock(), samplesValidatorMock);
    }

    private class KapaProtocolValidPredicateMock implements Predicate<Sample> {
        @Override
        public boolean test(Sample sample) {
            return sample.getIgoId().startsWith("valid");
        }
    }

    private class SamplesValidatorMock implements SamplesValidator {
        @Override
        public boolean isValid(Request request, Predicate<Sample> predicate) {
            return false;
        }

        @Override
        public Set<Sample> getNonValidSamples(Request request, Predicate<Sample> predicate) {
            return null;
        }
    }
}