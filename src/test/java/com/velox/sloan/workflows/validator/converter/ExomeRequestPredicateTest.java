package com.velox.sloan.workflows.validator.converter;

import com.velox.sloan.workflows.validator.TestUtils;
import org.junit.Test;
import org.mskcc.domain.KapaAgilentCaptureProtocol;
import org.mskcc.domain.Request;
import org.mskcc.domain.sample.Sample;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class ExomeRequestPredicateTest {
    private int id;
    private ExomeRequestPredicate exomeRequestPredicate = new ExomeRequestPredicate();

    @Test
    public void whenRequestNameContainsExomeLowerCase_shouldBeExomeRequest() {
        Request request = TestUtils.getRequestWithName("exome");

        boolean isExome = exomeRequestPredicate.test(request);

        assertThat(isExome, is(true));
    }

    @Test
    public void whenRequestNameContainsExomeUpperCase_shouldBeExomeRequest() {
        Request request = TestUtils.getRequestWithName("EXOME");

        boolean isExome = exomeRequestPredicate.test(request);

        assertThat(isExome, is(true));
    }

    @Test
    public void whenRequestNameContainsExomeFirstLetterUpperCase_shouldBeExomeRequest() {
        Request request = TestUtils.getRequestWithName("Exome");

        boolean isExome = exomeRequestPredicate.test(request);

        assertThat(isExome, is(true));
    }

    @Test
    public void whenRequestNameContainsExomeMixedCase_shouldBeExomeRequest() {
        Request request = TestUtils.getRequestWithName("eXomE");

        boolean isExome = exomeRequestPredicate.test(request);

        assertThat(isExome, is(true));
    }

    @Test
    public void whenRequestNameDoesntContainExome_shouldNotBeExomeRequest() {
        Request request = TestUtils.getRequestWithName("someOtherName");

        boolean isExome = exomeRequestPredicate.test(request);

        assertThat(isExome, is(false));
    }

    @Test
    public void whenRequestNameContainsWesLowerCase_shouldBeExomeRequest() {
        Request request = TestUtils.getRequestWithName("wes");

        boolean isExome = exomeRequestPredicate.test(request);

        assertThat(isExome, is(true));
    }

    @Test
    public void whenRequestNameContainsWesUpperCase_shouldBeExomeRequest() {
        Request request = TestUtils.getRequestWithName("WES");

        boolean isExome = exomeRequestPredicate.test(request);

        assertThat(isExome, is(true));
    }

    @Test
    public void whenRequestNameContainsWesFirstLetterUpperCase_shouldBeExomeRequest() {
        Request request = TestUtils.getRequestWithName("Wes");

        boolean isExome = exomeRequestPredicate.test(request);

        assertThat(isExome, is(true));
    }

    @Test
    public void whenRequestNameContainsWesMixedCase_shouldBeExomeRequest() {
        Request request = TestUtils.getRequestWithName("wEs");

        boolean isExome = exomeRequestPredicate.test(request);

        assertThat(isExome, is(true));
    }

    @Test
    public void whenRequestHasOneSampleWithHasKapaProtocol1_shouldBeExomeRequest() {
        Request request = new Request("3332");
        request.putSampleIfAbsent(getSampleWithKapaProtocols(1, 0));

        boolean isExome = exomeRequestPredicate.test(request);

        assertThat(isExome, is(true));
    }

    @Test
    public void whenRequestHasOneSampleWithHasKapaProtocol2_shouldBeExomeRequest() {
        Request request = new Request("3332");
        request.putSampleIfAbsent(getSampleWithKapaProtocols(0, 1));

        boolean isExome = exomeRequestPredicate.test(request);

        assertThat(isExome, is(true));
    }

    @Test
    public void whenRequestHasMultipleSampleAndOneWithHasKapaProtocol1_shouldBeExomeRequest() {
        Request request = new Request("3332");
        request.putSampleIfAbsent(getSampleWithKapaProtocols(1, 0));
        request.putSampleIfAbsent(getSample());
        request.putSampleIfAbsent(getSample());

        boolean isExome = exomeRequestPredicate.test(request);

        assertThat(isExome, is(true));
    }

    @Test
    public void whenRequestHasMultipleSampleAndOneWithHasKapaProtocol2_shouldBeExomeRequest() {
        Request request = new Request("3332");
        request.putSampleIfAbsent(getSample());

        request.putSampleIfAbsent(getSampleWithKapaProtocols(0, 1));

        boolean isExome = exomeRequestPredicate.test(request);

        assertThat(isExome, is(true));
    }

    @Test
    public void whenRequestHasMultipleSampleWithKapaProtocola1And2_shouldBeExomeRequest() {
        Request request = new Request("3332");
        request.putSampleIfAbsent(getSample());
        request.putSampleIfAbsent(getSampleWithKapaProtocols(2, 3));
        request.putSampleIfAbsent(getSampleWithKapaProtocols(1, 0));
        request.putSampleIfAbsent(getSampleWithKapaProtocols(0, 5));
        request.putSampleIfAbsent(getSampleWithKapaProtocols(123, 4343));

        boolean isExome = exomeRequestPredicate.test(request);

        assertThat(isExome, is(true));
    }

    @Test
    public void whenRequesNameContainsExomeAndRequesttHasKapaProtocols_shouldBeExomeRequest() {
        Request request = TestUtils.getRequestWithName("exome");
        request.putSampleIfAbsent(getSample());
        request.putSampleIfAbsent(getSampleWithKapaProtocols(2, 3));
        request.putSampleIfAbsent(getSampleWithKapaProtocols(1, 0));

        boolean isExome = exomeRequestPredicate.test(request);

        assertThat(isExome, is(true));
    }

    @Test
    public void whenRequesNameIsWesAndRequesttHasKapaProtocols_shouldBeExomeRequest() {
        Request request = TestUtils.getRequestWithName("wEs");
        request.putSampleIfAbsent(getSample());
        request.putSampleIfAbsent(getSampleWithKapaProtocols(1, 0));

        boolean isExome = exomeRequestPredicate.test(request);

        assertThat(isExome, is(true));
    }

    private Sample getSampleWithKapaProtocols(int noOfKapa1, int noOfKapa2) {
        Sample sample = getSample();
        sample.setKapaAgilentCaptureProtocols1(getKapaProtocols(noOfKapa1));
        sample.setKapaAgilentCaptureProtocols2(getKapaProtocols(noOfKapa2));
        return sample;
    }

    private List<KapaAgilentCaptureProtocol> getKapaProtocols(int noOfKapa) {
        List<KapaAgilentCaptureProtocol> kapa = new ArrayList<>();
        for (int i = 0; i < noOfKapa; i++) {
            kapa.add(new KapaAgilentCaptureProtocol());
        }

        return kapa;
    }


    private Sample getSample() {
        return new Sample(String.format("3322_%d", id++));
    }

}