package com.velox.sloan.workflows.validator.converter;

import com.velox.api.datarecord.DataRecord;
import com.velox.api.user.User;
import com.velox.sloan.cmo.staticstrings.datatypes.DT_Sample;
import org.junit.Before;
import org.junit.Test;
import org.mskcc.domain.Recipe;
import org.mskcc.domain.Sample;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SampleConverterTest {
    private User userMock = mock(User.class);
    private SampleConverter sampleConverter;

    @Before
    public void setUp() {
        sampleConverter = new SampleConverter(userMock);
    }

    @Test
    public void whenSampleRecordHasCorrectPropertiesSet_shouldReturnDomainSampleObjectWithThem() throws Exception {
        DataRecord sampleRecordMock = mock(DataRecord.class);
        String igoId = "12345_A_1";
        Recipe recipe = Recipe.AMPLI_SEQ;
        String reqId = "12345_A";

        when(sampleRecordMock.getStringVal(eq(DT_Sample.SAMPLE_ID), any())).thenReturn(igoId);
        when(sampleRecordMock.getStringVal(eq(DT_Sample.RECIPE), any())).thenReturn(recipe.getValue());
        when(sampleRecordMock.getStringVal(eq(DT_Sample.REQUEST_ID), any())).thenReturn(reqId);

        Sample sample = sampleConverter.convert(sampleRecordMock);

        assertThat(sample.getIgoId(), is(igoId));
        assertThat(sample.getRequestId(), is(reqId));
    }

    @Test
    public void whenSampleRecordHasUnsupportedRecipeSet_shouldAddMessageToNotificator() throws Exception {
        DataRecord sampleRecordMock = mock(DataRecord.class);
        String igoId = "12345_A_1";
        String reqId = "12345_A";

        when(sampleRecordMock.getStringVal(eq(DT_Sample.SAMPLE_ID), any())).thenReturn(igoId);
        when(sampleRecordMock.getStringVal(eq(DT_Sample.RECIPE), any())).thenReturn("unsupportedRecipe");
        when(sampleRecordMock.getStringVal(eq(DT_Sample.REQUEST_ID), any())).thenReturn(reqId);

        Sample sample = sampleConverter.convert(sampleRecordMock);
        assertThat(sample.getIgoId(), is(igoId));
        assertThat(sample.getRequestId(), is(reqId));

    }

    private Optional<Exception> assertThrown(Runnable runnable) {
        try {
            runnable.run();
            return Optional.empty();
        } catch (Exception e) {
            return Optional.of(e);
        }
    }
}