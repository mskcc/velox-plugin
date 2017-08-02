package com.velox.sloan.workflows.validator;

import org.hamcrest.object.IsCompatibleType;
import org.junit.Test;
import org.mskcc.domain.NimbleGenHybProtocol;
import org.mskcc.domain.sample.Sample;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class NimbleGenExistsPredicateTest {
    private NimbGenProtocolValidPredicate.NimbleGenExistsPredicate nimbleGenExistsPredicate = new NimbGenProtocolValidPredicate.NimbleGenExistsPredicate();

    @Test
    public void whenNoNimbGenProtocolsExist_shouldThrowAnException() {
        Sample sample = new Sample("2345_P_2");

        Optional<Exception> exception = org.mskcc.TestUtils.assertThrown(() -> nimbleGenExistsPredicate.test(sample));

        assertThat(exception.isPresent(), is(true));
        assertThat(exception.get().getClass(), IsCompatibleType.typeCompatibleWith(InvalidNimbleGenProtocolException.class));
    }

    @Test
    public void whenNimbGenProtocolsExist_shouldBeValid() {
        Sample sample = new Sample("2345_P_2");
        sample.addNimbleGenHybProtocol(new NimbleGenHybProtocol());

        boolean valid = nimbleGenExistsPredicate.test(sample);

        assertThat(valid, is(true));
    }
}