package com.velox.sloan.workflows.validator;

import org.hamcrest.object.IsCompatibleType;
import org.junit.Test;
import org.mskcc.domain.NimbleGenHybProtocol;
import org.mskcc.domain.sample.Sample;

import java.util.Date;
import java.util.Optional;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mskcc.util.TestUtils.assertThrown;

public class NimbleGenCreationDatePredicateTest {
    private NimbGenProtocolValidPredicate.NimbleGenCreationDatePredicate nimbleGenCreationDatePredicate = new NimbGenProtocolValidPredicate.NimbleGenCreationDatePredicate();

    @Test
    public void whenNoneNimbProtocolsHaveCreationDateSet_shouldThrowAnException() {
        Sample sample = new Sample("12345_P_6");
        sample.addNimbleGenHybProtocol(new NimbleGenHybProtocol());
        sample.addNimbleGenHybProtocol(new NimbleGenHybProtocol());
        sample.addNimbleGenHybProtocol(new NimbleGenHybProtocol());

        Optional<Exception> exception = assertThrown(() -> nimbleGenCreationDatePredicate.test(sample));

        assertThat(exception.isPresent(), is(true));
        assertThat(exception.get().getClass(), IsCompatibleType.typeCompatibleWith(InvalidNimbleGenProtocolException.class));
    }

    @Test
    public void whenOneNimbProtocolsHasCreationDateSet_shouldReturnTrue() {
        Sample sample = new Sample("12345_P_6");
        sample.addNimbleGenHybProtocol(getNimbleGenHybProtocolWithCreationDate());
        sample.addNimbleGenHybProtocol(new NimbleGenHybProtocol());
        sample.addNimbleGenHybProtocol(new NimbleGenHybProtocol());

        boolean valid = nimbleGenCreationDatePredicate.test(sample);

        assertThat(valid, is(true));
    }

    @Test
    public void whenAllNimbProtocolsHasCreationDateSet_shouldReturnTrue() {
        Sample sample = new Sample("12345_P_6");
        sample.addNimbleGenHybProtocol(getNimbleGenHybProtocolWithCreationDate());
        sample.addNimbleGenHybProtocol(getNimbleGenHybProtocolWithCreationDate());
        sample.addNimbleGenHybProtocol(getNimbleGenHybProtocolWithCreationDate());

        boolean valid = nimbleGenCreationDatePredicate.test(sample);

        assertThat(valid, is(true));
    }

    private NimbleGenHybProtocol getNimbleGenHybProtocolWithCreationDate() {
        NimbleGenHybProtocol nimbleGenHybProtocol = new NimbleGenHybProtocol();
        nimbleGenHybProtocol.setCreationDate(new Date());
        return nimbleGenHybProtocol;
    }
}