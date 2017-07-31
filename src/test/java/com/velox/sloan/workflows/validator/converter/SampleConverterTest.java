package com.velox.sloan.workflows.validator.converter;

import com.velox.api.datarecord.DataRecord;
import com.velox.api.datarecord.DataRecordManager;
import com.velox.api.datarecord.IoError;
import com.velox.api.datarecord.NotFound;
import com.velox.api.user.User;
import com.velox.sloan.cmo.staticstrings.datatypes.DT_Sample;
import com.velox.sloan.workflows.notificator.BulkNotificator;
import com.velox.sloan.workflows.validator.retriever.SampleRetriever;
import org.junit.Before;
import org.junit.Test;
import org.mskcc.domain.Recipe;
import org.mskcc.domain.sample.CmoSampleInfo;
import org.mskcc.domain.sample.Sample;
import org.mskcc.domain.sample.TumorNormalType;

import java.rmi.RemoteException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SampleConverterTest {
    private SampleConverter sampleConverter;
    private SampleRetriever sampleRetrieverMock = mock(SampleRetriever.class);

    @Before
    public void setUp() {
        sampleConverter = new SampleConverter(mock(User.class), mock(DataRecordManager.class), mock(BulkNotificator.class), sampleRetrieverMock);
    }

    @Test
    public void whenSampleRecordHasCorrectPropertiesSet_shouldReturnDomainSampleObjectWithThem() throws Exception {
        String igoId = "12345_A_1";
        String reqId = "12345_A";
        Recipe recipe = Recipe.AMPLI_SEQ;
        String sampleClass = "Tumor";
        String tumorOrNormal = "Tumor";
        String sampleInfoSampleClass = "Tumor";
        String sampleInfoTumorOrNormal = "Tumor";

        DataRecord sampleRecordMock = getSampleRecordMock(igoId, recipe.getValue(), reqId, tumorOrNormal, sampleClass, sampleInfoTumorOrNormal, sampleInfoSampleClass);

        Sample sample = sampleConverter.convert(sampleRecordMock);

        assertSampleHasData(sample, igoId, reqId, recipe, tumorOrNormal, sampleClass, sampleInfoSampleClass, sampleInfoTumorOrNormal);
    }

    @Test
    public void whenSampleRecordHasUnsupportedRecipeSet_shouldAddMessageToNotificator() throws Exception {
        String igoId = "12345_A_1";
        String reqId = "12345_A";
        String unsupportedRecipe = "unsupportedRecipe";
        String sampleClass = "Tumor";
        String tumorOrNormal = "Tumor";
        String sampleInfoSampleClass = "Normal";
        String sampleInfoTumorOrNormal = "Normal";

        DataRecord sampleRecordMock = getSampleRecordMock(igoId, unsupportedRecipe, reqId, tumorOrNormal, sampleClass, sampleInfoTumorOrNormal, sampleInfoSampleClass);
        when(sampleRecordMock.getChildrenOfType(any(), any())).thenReturn(new DataRecord[1]);

        Sample sample = sampleConverter.convert(sampleRecordMock);

        assertSampleHasData(sample, igoId, reqId, null, sampleClass, tumorOrNormal, sampleInfoSampleClass, sampleInfoTumorOrNormal);
    }

    private void assertSampleHasData(Sample sample, String igoId, String reqId, Recipe recipe, String cmoSampleClass, String tumorOrNormal, String sampleInfoSampleClass, String sampleInfoTumorOrNormal) {
        assertThat(sample.getIgoId(), is(igoId));
        assertThat(sample.getRequestId(), is(reqId));
        assertThat(sample.getRecipe(), is(recipe));
        assertThat(sample.getTumorNormalType(), is(TumorNormalType.getByValue(tumorOrNormal)));
        assertThat(sample.getSampleClass(), is(cmoSampleClass));
        assertThat(sample.getCmoSampleInfo().getSampleClass(), is(sampleInfoSampleClass));
        assertThat(sample.getCmoSampleInfo().getTumorNormalType(), is(TumorNormalType.getByValue(sampleInfoTumorOrNormal)));
    }

    private DataRecord getSampleRecordMock(String igoId, String recipe, String reqId, String tumorOrNormal, String cmoSampleClass, String sampleInfoTumorOrNormal, String sampleInfoSampleClass) throws NotFound, RemoteException, IoError {
        DataRecord sampleRecordMock = mock(DataRecord.class);
        when(sampleRecordMock.getStringVal(eq(DT_Sample.SAMPLE_ID), any())).thenReturn(igoId);
        when(sampleRecordMock.getStringVal(eq(DT_Sample.RECIPE), any())).thenReturn(recipe);
        when(sampleRecordMock.getStringVal(eq(DT_Sample.REQUEST_ID), any())).thenReturn(reqId);
        when(sampleRecordMock.getStringVal(eq(DT_Sample.TUMOR_OR_NORMAL), any())).thenReturn(tumorOrNormal);
        when(sampleRecordMock.getStringVal(eq(DT_Sample.CMO_SAMPLE_CLASS), any())).thenReturn(cmoSampleClass);
        when(sampleRecordMock.getChildrenOfType(any(), any())).thenReturn(new DataRecord[1]);

        CmoSampleInfo cmoSampleInfo = getCmoSampleInfo(sampleInfoTumorOrNormal, sampleInfoSampleClass);
        when(sampleRetrieverMock.getCmoSampleInfo(any())).thenReturn(cmoSampleInfo);

        return sampleRecordMock;
    }

    private CmoSampleInfo getCmoSampleInfo(String sampleInfoTumorOrNormal, String sampleInfoSampleClass) {
        CmoSampleInfo cmoSampleInfo = new CmoSampleInfo();
        cmoSampleInfo.setTumorNormalType(TumorNormalType.getByValue(sampleInfoTumorOrNormal));
        cmoSampleInfo.setSampleClass(sampleInfoSampleClass);
        return cmoSampleInfo;
    }
}