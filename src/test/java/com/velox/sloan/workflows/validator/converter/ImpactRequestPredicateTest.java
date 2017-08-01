package com.velox.sloan.workflows.validator.converter;

import com.velox.sloan.workflows.validator.TestUtils;
import org.junit.Test;
import org.mskcc.domain.NimbleGenHybProtocol;
import org.mskcc.domain.Request;
import org.mskcc.domain.sample.Sample;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class ImpactRequestPredicateTest {
    private ImpactRequestPredicate impactRequestPredicate = new ImpactRequestPredicate();

    @Test
    public void whenRequestNameContainssPactLowerCase_shouldBeImpactRequest() {
        Request request = TestUtils.getRequestWithName("pact");

        boolean isImpact = impactRequestPredicate.test(request);

        assertThat(isImpact, is(true));
    }

    @Test
    public void whenRequestNameContainssPactUpperCase_shouldBeImpactRequest() {
        Request request = TestUtils.getRequestWithName("PACT");

        boolean isImpact = impactRequestPredicate.test(request);

        assertThat(isImpact, is(true));
    }

    @Test
    public void whenRequestNameContainssPactFirstLetterUpperCase_shouldBeImpactRequest() {
        Request request = TestUtils.getRequestWithName("Pact");

        boolean isImpact = impactRequestPredicate.test(request);

        assertThat(isImpact, is(true));
    }

    @Test
    public void whenRequestNameContainssPactMixedCase_shouldBeImpactRequest() {
        Request request = TestUtils.getRequestWithName("paCt");

        boolean isImpact = impactRequestPredicate.test(request);

        assertThat(isImpact, is(true));
    }

    @Test
    public void whenRequestNameDoesntContainsPact_shouldNotBeImpactRequest() {
        Request request = TestUtils.getRequestWithName("somethingTotallyDifferent");

        boolean isImpact = impactRequestPredicate.test(request);

        assertThat(isImpact, is(false));
    }

    @Test
    public void whenRequestIsInnovation_shouldBeImpactRequest() {
        Request request = new Request("432");
        request.setInnovation(true);

        boolean isImpact = impactRequestPredicate.test(request);

        assertThat(isImpact, is(true));
    }

    @Test
    public void whenRequestHasNibleGenProtocol_shouldBeImpactRequest() {
        Request request = new Request("432");
        Sample sample = new Sample("432");
        sample.addNimbleGenHybProtocol(new NimbleGenHybProtocol());
        request.putSampleIfAbsent(sample);

        boolean isImpact = impactRequestPredicate.test(request);

        assertThat(isImpact, is(true));
    }

    @Test
    public void whenRequestHasMultipleSampleAndSomeHaveNibleGenProtocol_shouldBeImpactRequest() {
        Request request = new Request("432");
        Sample sample = new Sample("432");
        sample.addNimbleGenHybProtocol(new NimbleGenHybProtocol());
        request.putSampleIfAbsent(sample);
        request.putSampleIfAbsent(new Sample("432"));
        request.putSampleIfAbsent(new Sample("43432"));

        boolean isImpact = impactRequestPredicate.test(request);

        assertThat(isImpact, is(true));
    }


}