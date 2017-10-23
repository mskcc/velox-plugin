package com.velox.sloan.workflows.validator;

import org.hamcrest.object.IsCompatibleType;
import org.junit.Test;
import org.mskcc.domain.NimbleGenHybProtocol;
import org.mskcc.domain.sample.Sample;

import java.util.Optional;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mskcc.util.TestUtils.assertThrown;

public class NimbleGenSampleIdsPredicateTest {
    private NimbGenProtocolValidPredicate.NimbleGenSampleIdsPredicate nimbleGenSampleIdsPredicate = new NimbGenProtocolValidPredicate.NimbleGenSampleIdsPredicate();

    @Test
    public void whenSampleHasSameIdAsNimbProtocol_shouldReturnTrue() {
        String id = "1234_P";
        Sample sample = new Sample(id);
        sample.addNimbleGenHybProtocol(getNimbleGenHybProtocolWithId(id));

        boolean valid = nimbleGenSampleIdsPredicate.test(sample);

        assertThat(valid, is(true));
    }

    @Test
    public void whenMultipleNimbProtocolsHaveSameIdThanSample_shouldReturnTrue() {
        String id = "1234_P";
        Sample sample = new Sample(id);
        sample.addNimbleGenHybProtocol(getNimbleGenHybProtocolWithId(id));
        sample.addNimbleGenHybProtocol(getNimbleGenHybProtocolWithId(id));
        sample.addNimbleGenHybProtocol(getNimbleGenHybProtocolWithId(id));

        boolean valid = nimbleGenSampleIdsPredicate.test(sample);

        assertThat(valid, is(true));
    }

    @Test
    public void whenSampleHasDifferentIdAsNimbProtocol_shouldThrowAnException() {
        String id = "1234_P";
        Sample sample = new Sample(id);
        sample.addNimbleGenHybProtocol(getNimbleGenHybProtocolWithId("some_different_id"));

        Optional<Exception> exception = assertThrown(() -> nimbleGenSampleIdsPredicate.test(sample));

        assertThat(exception.isPresent(), is(true));
        assertThat(exception.get().getClass(), IsCompatibleType.typeCompatibleWith(InvalidNimbleGenProtocolException.class));
    }

    @Test
    public void whenMultipleNimbProtocolsHaveDifferentIdThanSample_shouldThrowAnException() {
        String id = "1234_P";
        Sample sample = new Sample(id);
        sample.addNimbleGenHybProtocol(getNimbleGenHybProtocolWithId("some_different_id"));
        sample.addNimbleGenHybProtocol(getNimbleGenHybProtocolWithId("yet_another_different_id"));
        sample.addNimbleGenHybProtocol(getNimbleGenHybProtocolWithId("0984324"));

        Optional<Exception> exception = assertThrown(() -> nimbleGenSampleIdsPredicate.test(sample));

        assertThat(exception.isPresent(), is(true));
        assertThat(exception.get().getClass(), IsCompatibleType.typeCompatibleWith(InvalidNimbleGenProtocolException.class));
    }

    @Test
    public void whenAllButOneNimbProtocolsHaveDifferentIdThanSample_shouldThrowAnException() {
        String id = "1234_P";
        Sample sample = new Sample(id);
        sample.addNimbleGenHybProtocol(getNimbleGenHybProtocolWithId(id));
        sample.addNimbleGenHybProtocol(getNimbleGenHybProtocolWithId("yet_another_different_id"));
        sample.addNimbleGenHybProtocol(getNimbleGenHybProtocolWithId("0984324"));

        Optional<Exception> exception = assertThrown(() -> nimbleGenSampleIdsPredicate.test(sample));

        assertThat(exception.isPresent(), is(true));
        assertThat(exception.get().getClass(), IsCompatibleType.typeCompatibleWith(InvalidNimbleGenProtocolException.class));
    }

    @Test
    public void whenAllButOneNimbProtocolsHaveSameIdThanSample_shouldThrowAnException() {
        String id = "1234_P";
        Sample sample = new Sample(id);
        sample.addNimbleGenHybProtocol(getNimbleGenHybProtocolWithId(id));
        sample.addNimbleGenHybProtocol(getNimbleGenHybProtocolWithId(id));
        sample.addNimbleGenHybProtocol(getNimbleGenHybProtocolWithId(id));
        sample.addNimbleGenHybProtocol(getNimbleGenHybProtocolWithId("0984324"));

        Optional<Exception> exception = assertThrown(() -> nimbleGenSampleIdsPredicate.test(sample));

        assertThat(exception.isPresent(), is(true));
        assertThat(exception.get().getClass(), IsCompatibleType.typeCompatibleWith(InvalidNimbleGenProtocolException.class));
    }

    private NimbleGenHybProtocol getNimbleGenHybProtocolWithId(String id) {
        NimbleGenHybProtocol nimbleGenHybProtocol = new NimbleGenHybProtocol();
        nimbleGenHybProtocol.setIgoSampleId(id);
        return nimbleGenHybProtocol;
    }
}