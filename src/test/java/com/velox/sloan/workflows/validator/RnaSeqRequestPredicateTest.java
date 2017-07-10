package com.velox.sloan.workflows.validator;

import org.junit.Test;
import org.mskcc.domain.Protocol;
import org.mskcc.domain.Request;
import org.mskcc.domain.sample.Sample;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class RnaSeqRequestPredicateTest {
    RnaSeqRequestPredicate rnaSeqRequestPredicate = new RnaSeqRequestPredicate();

    @Test
    public void whenRequestDoesntContainSampleWithRnaSeqProtocol_shouldReturnFalse() {
        Request request = new Request("01234_P");

        boolean isRnaSeq = rnaSeqRequestPredicate.test(request);

        assertThat(isRnaSeq, is(false));
    }

    @Test
    public void whenRequestContainsSampleWithRnaSeqProtocol_shouldReturnTrue() {
        Request request = new Request("01234_P");
        Sample rnaSeqSample = new Sample("012345_P_1");
        rnaSeqSample.addProtocol(Protocol.SMAR_TER_AMPLIFICATION_PROTOCOL_1);
        request.putSampleIfAbsent(rnaSeqSample);

        boolean isRnaSeq = rnaSeqRequestPredicate.test(request);

        assertThat(isRnaSeq, is(true));
    }

}