package com.velox.sloan.workflows.validator;

import org.junit.Before;
import org.junit.Test;
import org.mskcc.domain.Protocol;
import org.mskcc.domain.sample.Sample;
import org.mskcc.util.VeloxConstants;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class DnaLibraryProtocolValidPredicateTest {
    private final DnaLibraryProtocolValidator.DnaLibraryProtocolValidPredicate dnaLibraryProtocolValidPredicate = new DnaLibraryProtocolValidator.DnaLibraryProtocolValidPredicate();
    private Sample sample;

    @Before
    public void setUp() throws Exception {
        sample = new Sample("12345_P_1");
    }

    @Test
    public void whenSampleHasNoDnaLibraryProtocols_shouldBeInvalid() {
        boolean valid = dnaLibraryProtocolValidPredicate.test(sample);

        assertThat(valid, is(false));
    }

    @Test
    public void whenSampleHasOneDnaLibraryProtocol2WithValidityNotSet_shouldBeInvalid() {
        sample.getProtocols().put(VeloxConstants.DNA_LIBRARY_PREP_PROTOCOL_2, getNoValidityNoElutionProtocol());

        boolean valid = dnaLibraryProtocolValidPredicate.test(sample);

        assertThat(valid, is(false));
    }

    @Test
    public void whenSampleHasOneDnaLibraryProtocol2WithValidityNotSetAndCorrectElutionVolume_shouldBeInvalid() {
        sample.getProtocols().put(VeloxConstants.DNA_LIBRARY_PREP_PROTOCOL_2, getEmptyValidityCorrectElutionProtocol());

        boolean valid = dnaLibraryProtocolValidPredicate.test(sample);

        assertThat(valid, is(false));
    }

    @Test
    public void whenSampleHasOneInvalidDnaLibraryProtocol2WithElutionVolume_shouldBeInvalid() {
        sample.getProtocols().put(VeloxConstants.DNA_LIBRARY_PREP_PROTOCOL_2, getInvalidCorrectElutionProtocol());

        boolean valid = dnaLibraryProtocolValidPredicate.test(sample);

        assertThat(valid, is(false));
    }

    @Test
    public void whenSampleHasOneValidDnaLibraryProtocol2WithElutionVolume_shouldBeValid() {
        sample.getProtocols().put(VeloxConstants.DNA_LIBRARY_PREP_PROTOCOL_2, getValidCorrectElutionProtocol());

        boolean valid = dnaLibraryProtocolValidPredicate.test(sample);

        assertThat(valid, is(true));
    }

    @Test
    public void whenSampleHasOneDnaLibraryProtocol3WithValidityNotSet_shouldBeInvalid() {
        sample.getProtocols().put(VeloxConstants.DNA_LIBRARY_PREP_PROTOCOL_3, getNoValidityNoElutionProtocol());

        boolean valid = dnaLibraryProtocolValidPredicate.test(sample);

        assertThat(valid, is(false));
    }

    @Test
    public void whenSampleHasOneDnaLibraryProtocol3WithValidityNotSetAndCorrectElutionVolume_shouldBeInvalid() {
        sample.getProtocols().put(VeloxConstants.DNA_LIBRARY_PREP_PROTOCOL_3, getEmptyValidityCorrectElutionProtocol());

        boolean valid = dnaLibraryProtocolValidPredicate.test(sample);

        assertThat(valid, is(false));
    }

    @Test
    public void whenSampleHasOneInvalidDnaLibraryProtocol3WithElutionVolume_shouldBeInvalid() {
        sample.getProtocols().put(VeloxConstants.DNA_LIBRARY_PREP_PROTOCOL_3, getInvalidCorrectElutionProtocol());

        boolean valid = dnaLibraryProtocolValidPredicate.test(sample);

        assertThat(valid, is(false));
    }

    @Test
    public void whenSampleHasOneValidDnaLibraryProtocol3WithElutionVolume_shouldBeValid() {
        sample.getProtocols().put(VeloxConstants.DNA_LIBRARY_PREP_PROTOCOL_3, getValidCorrectElutionProtocol());

        boolean valid = dnaLibraryProtocolValidPredicate.test(sample);

        assertThat(valid, is(true));
    }

    @Test
    public void whenSampleHasTwoCorrectProtocols_shouldBeValid() {
        sample.getProtocols().put(VeloxConstants.DNA_LIBRARY_PREP_PROTOCOL_2, getValidCorrectElutionProtocol());
        sample.getProtocols().put(VeloxConstants.DNA_LIBRARY_PREP_PROTOCOL_3, getValidCorrectElutionProtocol());

        boolean valid = dnaLibraryProtocolValidPredicate.test(sample);

        assertThat(valid, is(true));
    }

    @Test
    public void whenSampleHasOneCorrectOneIncorrectSameTypeProtocol_shouldBeValid() {
        sample.getProtocols().put(VeloxConstants.DNA_LIBRARY_PREP_PROTOCOL_2, getValidCorrectElutionProtocol());
        sample.getProtocols().put(VeloxConstants.DNA_LIBRARY_PREP_PROTOCOL_2, getInvalidCorrectElutionProtocol());

        boolean valid = dnaLibraryProtocolValidPredicate.test(sample);

        assertThat(valid, is(true));
    }

    @Test
    public void whenSampleHasOneCorrectOneIncorrectDifferentTypeProtocol_shouldBeValid() {
        sample.getProtocols().put(VeloxConstants.DNA_LIBRARY_PREP_PROTOCOL_2, getValidCorrectElutionProtocol());
        sample.getProtocols().put(VeloxConstants.DNA_LIBRARY_PREP_PROTOCOL_3, getInvalidCorrectElutionProtocol());

        boolean valid = dnaLibraryProtocolValidPredicate.test(sample);

        assertThat(valid, is(true));
    }

    @Test
    public void whenSampleHasAllInvalidProtocols_shouldBeInvalid() {
        sample.getProtocols().put(VeloxConstants.DNA_LIBRARY_PREP_PROTOCOL_2, getEmptyValidityCorrectElutionProtocol());
        sample.getProtocols().put(VeloxConstants.DNA_LIBRARY_PREP_PROTOCOL_2, getInvalidCorrectElutionProtocol());
        sample.getProtocols().put(VeloxConstants.DNA_LIBRARY_PREP_PROTOCOL_3, getNoValidityNoElutionProtocol());
        sample.getProtocols().put(VeloxConstants.DNA_LIBRARY_PREP_PROTOCOL_3, getInvalidNoElutionProtocol());
        sample.getProtocols().put(VeloxConstants.DNA_LIBRARY_PREP_PROTOCOL_3, getInvalidCorrectElutionProtocol());

        boolean valid = dnaLibraryProtocolValidPredicate.test(sample);

        assertThat(valid, is(false));
    }

    @Test
    public void whenSampleHasAllValidProtocolsAndOneInvalidOfDifferentType_shouldBeValid() {
        sample.getProtocols().put(VeloxConstants.DNA_LIBRARY_PREP_PROTOCOL_2, getValidCorrectElutionProtocol());
        sample.getProtocols().put(VeloxConstants.DNA_LIBRARY_PREP_PROTOCOL_2, getValidCorrectElutionProtocol());
        sample.getProtocols().put(VeloxConstants.DNA_LIBRARY_PREP_PROTOCOL_2, getValidCorrectElutionProtocol());
        sample.getProtocols().put(VeloxConstants.DNA_LIBRARY_PREP_PROTOCOL_2, getValidCorrectElutionProtocol());
        sample.getProtocols().put(VeloxConstants.DNA_LIBRARY_PREP_PROTOCOL_3, getInvalidCorrectElutionProtocol());

        boolean valid = dnaLibraryProtocolValidPredicate.test(sample);

        assertThat(valid, is(true));
    }

    private Protocol getEmptyValidityCorrectElutionProtocol() {
        Protocol protocol = getNoValidityNoElutionProtocol();
        protocol.getProtocolFields().put(VeloxConstants.ELUTION_VOL, 3);

        return protocol;
    }

    private Protocol getInvalidCorrectElutionProtocol() {
        Protocol protocol = getInvalidNoElutionProtocol();
        protocol.setValid(false);
        protocol.getProtocolFields().put(VeloxConstants.ELUTION_VOL, 3);

        return protocol;
    }

    private Protocol getValidCorrectElutionProtocol() {
        Protocol protocol = getValidNoElutionProtocol();
        protocol.getProtocolFields().put(VeloxConstants.ELUTION_VOL, 3);

        return protocol;
    }

    private Protocol getValidNoElutionProtocol() {
        Protocol protocol = getNoValidityNoElutionProtocol();
        protocol.setValid(true);
        return protocol;
    }

    private Protocol getInvalidNoElutionProtocol() {
        Protocol protocol = getNoValidityNoElutionProtocol();
        protocol.setValid(false);
        return protocol;
    }

    private Protocol getNoValidityNoElutionProtocol() {
        return new Protocol();
    }


}