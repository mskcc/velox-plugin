package com.velox.sloan.workflows.validator;

import org.hamcrest.object.IsCompatibleType;
import org.junit.Test;
import org.mskcc.domain.NimbleGenHybProtocol;
import org.mskcc.domain.sample.Sample;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mskcc.util.TestUtils.assertThrown;

public class NimbleGenValidPredicateTest {
    private NimbGenProtocolValidPredicate.NimbleGenValidPredicate nimbleGenValidPredicate = new NimbGenProtocolValidPredicate.NimbleGenValidPredicate();

    @Test
    public void whenNoNimbGenProtocolHasValidFieldSet_shouldThrowAnException() {
        Sample sample = new Sample("2345_P_2");
        sample.addNimbleGenHybProtocol(new NimbleGenHybProtocol());
        sample.addNimbleGenHybProtocol(new NimbleGenHybProtocol());

        Optional<Exception> exception = assertThrown(() -> nimbleGenValidPredicate.test(sample));

        assertThat(exception.isPresent(), is(true));
        assertThat(exception.get().getClass(), IsCompatibleType.typeCompatibleWith(InvalidNimbleGenProtocolException.class));
    }

    @Test
    public void whenNoneNimbleGenProtocolHasValidFieldSetToTrue_shouldThrowAnException() {
        Sample sample = new Sample("2345_P_2");
        sample.addNimbleGenHybProtocol(getInvalidNimbleGenProtocol());
        sample.addNimbleGenHybProtocol(getInvalidNimbleGenProtocol());

        Optional<Exception> exception = assertThrown(() -> nimbleGenValidPredicate.test(sample));

        assertThat(exception.isPresent(), is(true));
        assertThat(exception.get().getClass(), IsCompatibleType.typeCompatibleWith(InvalidNimbleGenProtocolException.class));
    }

    @Test
    public void whenOneNimbleGenProtocolHasValidFieldSetToTrue_shouldThrowAnException() {
        Sample sample = new Sample("2345_P_2");
        sample.addNimbleGenHybProtocol(getInvalidNimbleGenProtocol());
        sample.addNimbleGenHybProtocol(getInvalidNimbleGenProtocol());

        Optional<Exception> exception = assertThrown(() -> nimbleGenValidPredicate.test(sample));

        assertThat(exception.isPresent(), is(true));
        assertThat(exception.get().getClass(), IsCompatibleType.typeCompatibleWith(InvalidNimbleGenProtocolException.class));
    }

    private NimbleGenHybProtocol getInvalidNimbleGenProtocol() {
        NimbleGenHybProtocol nimbleGenHybProtocol = new NimbleGenHybProtocol();
        nimbleGenHybProtocol.setValid(false);
        return nimbleGenHybProtocol;
    }

}