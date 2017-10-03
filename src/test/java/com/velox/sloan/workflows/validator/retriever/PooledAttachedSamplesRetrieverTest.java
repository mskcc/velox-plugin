package com.velox.sloan.workflows.validator.retriever;

import com.velox.api.datarecord.DataRecord;
import com.velox.api.datarecord.IoError;
import com.velox.api.datarecord.NotFound;
import com.velox.api.user.User;
import com.velox.api.workflow.ActiveTask;
import com.velox.sloan.cmo.staticstrings.datatypes.DT_Sample;
import org.junit.Before;
import org.junit.Test;
import org.mskcc.util.Constants;

import java.rmi.RemoteException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PooledAttachedSamplesRetrieverTest {
    private ActiveTask activeTaskMock;
    private User userMock;
    private PooledAttachedSamplesRetriever pooledAttachedSamplesRetriever;
    private Random random = new Random();

    @Before
    public void setUp() throws Exception {
        activeTaskMock = mock(ActiveTask.class);
        userMock = mock(User.class);
        pooledAttachedSamplesRetriever = new PooledAttachedSamplesRetriever(activeTaskMock, userMock);
    }

    @Test
    public void whenPoolsAreAttached_shouldReturnSamplesFromAllOfThem() throws Exception {
        assertAttachedSamples(Arrays.asList(0));
        assertAttachedSamples(Arrays.asList(0, 0, 0));
        assertAttachedSamples(Arrays.asList(1));
        assertAttachedSamples(Arrays.asList(1, 1));
        assertAttachedSamples(Arrays.asList(2));
        assertAttachedSamples(Arrays.asList(3));
        assertAttachedSamples(Arrays.asList(2, 3));
        assertAttachedSamples(Arrays.asList(12, 4, 0));
        assertAttachedSamples(Arrays.asList(412, 4, 540, 5, 3, 45, 2, 43));
    }

    @Test
    public void whenThereAreNoAttachedSamples_shouldReturnNoAttachedSamples() throws Exception {
        //given
        when(activeTaskMock.getAttachedDataRecords(DT_Sample.DATA_TYPE, userMock)).thenReturn(Collections.emptyList());

        //when
        List<DataRecord> attachedSamples = pooledAttachedSamplesRetriever.retrieve();

        //then
        assertThat(attachedSamples.size(), is(0));
    }

    @Test
    public void whenThereIsOneNonPoolAttachedSample_shouldReturnNoAttachedSamples() throws Exception {
        //given
        List<DataRecord> samples = Arrays.asList(getSampleRecordMock());
        when(activeTaskMock.getAttachedDataRecords(DT_Sample.DATA_TYPE, userMock)).thenReturn(samples);

        //when
        List<DataRecord> attachedSamples = pooledAttachedSamplesRetriever.retrieve();

        //then
        assertThat(attachedSamples.size(), is(0));
    }

    @Test
    public void whenThereAreMultipleNonPoolAttachedSamples_shouldReturnNoAttachedSamples() throws Exception {
        //given
        List<DataRecord> samples = Arrays.asList(getSampleRecordMock(), getSampleRecordMock(), getSampleRecordMock());
        when(activeTaskMock.getAttachedDataRecords(DT_Sample.DATA_TYPE, userMock)).thenReturn(samples);

        //when
        List<DataRecord> attachedSamples = pooledAttachedSamplesRetriever.retrieve();

        //then
        assertThat(attachedSamples.size(), is(0));
    }

    @Test
    public void whenThereIsOnePoolWithOneSample_shouldReturnOneAttachedSamples() throws Exception {
        //given
        List<DataRecord> samples = Arrays.asList(new PoolMockBuilder().withSamples(1).build());
        when(activeTaskMock.getAttachedDataRecords(DT_Sample.DATA_TYPE, userMock)).thenReturn(samples);

        //when
        List<DataRecord> attachedSamples = pooledAttachedSamplesRetriever.retrieve();

        //then
        assertThat(attachedSamples.size(), is(1));
    }

    @Test
    public void whenThereIsOnePoolWithFourSamples_shouldReturnFourAttachedSamples() throws Exception {
        //given
        List<DataRecord> samples = Arrays.asList(new PoolMockBuilder().withSamples(4).build());
        when(activeTaskMock.getAttachedDataRecords(DT_Sample.DATA_TYPE, userMock)).thenReturn(samples);

        //when
        List<DataRecord> attachedSamples = pooledAttachedSamplesRetriever.retrieve();

        //then
        assertThat(attachedSamples.size(), is(4));
    }

    @Test
    public void whenThereAreMultiplePoolsEachWithOneSample_shouldReturnAllOfThemAsAttachedSamples() throws Exception {
        //given
        List<DataRecord> samples = Arrays.asList(
                new PoolMockBuilder().withSamples(1).build(),
                new PoolMockBuilder().withSamples(1).build(),
                new PoolMockBuilder().withSamples(1).build(),
                new PoolMockBuilder().withSamples(1).build());
        when(activeTaskMock.getAttachedDataRecords(DT_Sample.DATA_TYPE, userMock)).thenReturn(samples);

        //when
        List<DataRecord> attachedSamples = pooledAttachedSamplesRetriever.retrieve();

        //then
        assertThat(attachedSamples.size(), is(1 + 1 + 1 + 1));
    }

    @Test
    public void whenThereAreMultiplePoolsWithMultipleSample_shouldReturnAllOfThemAsAttachedSamples() throws Exception {
        //given
        List<DataRecord> samples = Arrays.asList(
                new PoolMockBuilder().withSamples(1).build(),
                new PoolMockBuilder().withSamples(43).build(),
                new PoolMockBuilder().withSamples(5).build(),
                new PoolMockBuilder().withSamples(13).build());
        when(activeTaskMock.getAttachedDataRecords(DT_Sample.DATA_TYPE, userMock)).thenReturn(samples);

        //when
        List<DataRecord> attachedSamples = pooledAttachedSamplesRetriever.retrieve();

        //then
        assertThat(attachedSamples.size(), is(1 + 43 + 5 + 13));
    }

    @Test
    public void whenThereIsOnePoolWithOnePool_shouldReturnNoAttachedSamples() throws Exception {
        //given
        List<DataRecord> samples = Arrays.asList(new PoolMockBuilder().withPools(1).build());
        when(activeTaskMock.getAttachedDataRecords(DT_Sample.DATA_TYPE, userMock)).thenReturn(samples);

        //when
        List<DataRecord> attachedSamples = pooledAttachedSamplesRetriever.retrieve();

        //then
        assertThat(attachedSamples.size(), is(0));
    }

    @Test
    public void whenThereIsOnePoolWithOnePoolAndOneSample_shouldReturnOneAttachedSample() throws Exception {
        //given
        DataRecord poolRecord = new PoolMockBuilder()
                .withPools(1)
                .withSamples(1)
                .build();

        List<DataRecord> samples = Arrays.asList(poolRecord);
        when(activeTaskMock.getAttachedDataRecords(DT_Sample.DATA_TYPE, userMock)).thenReturn(samples);

        //when
        List<DataRecord> attachedSamples = pooledAttachedSamplesRetriever.retrieve();

        //then
        assertThat(attachedSamples.size(), is(1));
    }

    private DataRecord getSampleRecordMock() throws NotFound, RemoteException {
        DataRecord sampleMock = mock(DataRecord.class);
        when(sampleMock.getStringVal(DT_Sample.SAMPLE_ID, userMock)).thenReturn("12345_B" + getRandom());

        return sampleMock;
    }

    private DataRecord getPoolRecordMock() throws Exception {
        DataRecord poolMock = mock(DataRecord.class);
        when(poolMock.getStringVal(DT_Sample.SAMPLE_ID, userMock)).thenReturn(Constants.POOL_PREFIX + getRandom());

        return poolMock;
    }

    private void assertAttachedSamples(List<Integer> sampleCounts) throws Exception {
        //given
        List<DataRecord> pools = getAttachedPools(sampleCounts);
        when(activeTaskMock.getAttachedDataRecords(DT_Sample.DATA_TYPE, userMock)).thenReturn(pools);

        //when
        List<DataRecord> attachedSamples = pooledAttachedSamplesRetriever.retrieve();

        //then
        int expectedNumberOfAttachedSamples = sampleCounts.stream().mapToInt(s -> s.intValue()).sum();
        assertThat(attachedSamples.size(), is(expectedNumberOfAttachedSamples));

        List<Integer> ids = IntStream.range(0, expectedNumberOfAttachedSamples).boxed().collect(Collectors.toList());

        for (DataRecord attachedSample : attachedSamples) {
            ids.remove(Integer.valueOf(attachedSample.getStringVal(DT_Sample.SAMPLE_ID, userMock)));
        }

        assertThat(ids.size(), is(0));
    }

    private List<DataRecord> getAttachedPools(List<Integer> sampleCounts) throws NotFound, RemoteException, IoError {
        int id = 0;
        int poolId = 0;
        List<DataRecord> pools = new ArrayList<>();
        for (int sampleCount : sampleCounts) {
            DataRecord poolMock = mock(DataRecord.class);

            List<DataRecord> parentSamples = new ArrayList<>();
            for (int j = 0; j < sampleCount; j++) {
                DataRecord sampleMock = mock(DataRecord.class);
                when(sampleMock.getStringVal(DT_Sample.SAMPLE_ID, userMock)).thenReturn(String.valueOf(id++));
                parentSamples.add(sampleMock);
            }

            when(poolMock.getParentsOfType(DT_Sample.DATA_TYPE, userMock)).thenReturn(parentSamples);
            when(poolMock.getStringVal(DT_Sample.SAMPLE_ID, userMock)).thenReturn("Pool-" + poolId++);

            pools.add(poolMock);
        }
        return pools;
    }

    public String getRandom() {
        return String.valueOf(random.nextInt());
    }

    class PoolMockBuilder {
        private int numberOfSamples;
        private int numberOfPools;

        public PoolMockBuilder withSamples(int numberOfSamples) {
            this.numberOfSamples = numberOfSamples;
            return this;
        }

        public PoolMockBuilder withPools(int numberOfPools) {
            this.numberOfPools = numberOfPools;
            return this;
        }

        public DataRecord build() throws Exception {
            DataRecord poolMock = mock(DataRecord.class);
            when(poolMock.getStringVal(DT_Sample.SAMPLE_ID, userMock)).thenReturn(Constants.POOL_PREFIX + getRandom());
            List<DataRecord> parentSamples = getParentSamples();
            when(poolMock.getParentsOfType(DT_Sample.DATA_TYPE, userMock)).thenReturn(parentSamples);

            return poolMock;
        }

        private List<DataRecord> getParentSamples() throws Exception {
            List<DataRecord> parentSamples = new ArrayList<>();
            for (int i = 0; i < numberOfSamples; i++)
                parentSamples.add(getSampleRecordMock());

            for (int i = 0; i < numberOfPools; i++)
                parentSamples.add(getPoolRecordMock());
            return parentSamples;
        }
    }
}