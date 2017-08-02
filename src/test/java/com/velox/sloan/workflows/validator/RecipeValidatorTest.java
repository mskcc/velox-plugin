package com.velox.sloan.workflows.validator;

import com.velox.api.datarecord.DataRecord;
import com.velox.api.user.User;
import com.velox.sloan.workflows.notificator.BulkNotificator;
import org.junit.Before;
import org.junit.Test;
import org.mskcc.domain.Recipe;
import org.mskcc.domain.Request;
import org.mskcc.domain.sample.Sample;

import java.util.Collections;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.mock;

public class RecipeValidatorTest {
    private Request request;
    private BulkNotificator notificator = mock(BulkNotificator.class);
    private final RecipeValidator recipeValidator = new RecipeValidator(notificator);
    private User user = mock(User.class);
    private Map<String, DataRecord> sampleIdToRecords = Collections.emptyMap();

    @Before
    public void setUp() {
        request = new Request("someId");
    }

    @Test
    public void whenRequestHasNoSamples_shouldReturnTrue() {
        assertThat(recipeValidator.isValid(request), is(true));
    }

    @Test
    public void whenRequestHasOneSampleWithoutRecipe_shouldReturnFalse() {
        Sample sample = new Sample("45435_D");
        request.putSampleIfAbsent(sample);

        assertThat(recipeValidator.isValid(request), is(false));
    }

    @Test
    public void whenNoneOfTheSamplesHaveRecipe_shouldReturnFalse() {
        request.putSampleIfAbsent(new Sample("45435_D"));
        request.putSampleIfAbsent(new Sample("11135_D"));
        request.putSampleIfAbsent(new Sample("4324435_D"));

        assertThat(recipeValidator.isValid(request), is(false));
    }

    @Test
    public void whenRequestHasOneSampleWithRecipe_shouldReturnTrue() {
        Sample sample = getSample("5435434543", Recipe.AMPLI_SEQ);
        request.putSampleIfAbsent(sample);

        assertThat(recipeValidator.isValid(request), is(true));
    }

    @Test
    public void whenAllOfTheSamplesHaveRecipe_shouldReturnTrue() {
        request.putSampleIfAbsent(getSample("123", Recipe.AMPLI_SEQ));
        request.putSampleIfAbsent(getSample("432", Recipe.AMPLI_SEQ));
        request.putSampleIfAbsent(getSample("54545", Recipe.AMPLI_SEQ));
        request.putSampleIfAbsent(getSample("444", Recipe.AMPLI_SEQ));

        assertThat(recipeValidator.isValid(request), is(true));
    }

    @Test
    public void whenAllButOneOfTheSamplesHaveRecipe_shouldReturnFalse() {
        request.putSampleIfAbsent(getSample("123", Recipe.AMPLI_SEQ));
        request.putSampleIfAbsent(getSample("432", Recipe.AMPLI_SEQ));
        request.putSampleIfAbsent(new Sample("54543"));
        request.putSampleIfAbsent(getSample("444", Recipe.AMPLI_SEQ));

        assertThat(recipeValidator.isValid(request), is(false));
    }

    @Test
    public void whenRecipesContainOneValue_shouldReturnTrue() {
        Sample sample = getSample("id1", Recipe.RNA_SEQ_POLY_A);
        request.putSampleIfAbsent(sample);
        assertThat(recipeValidator.isValid(request), is(true));
    }

    @Test
    public void whenRecipesContainTwoSameValues_shouldReturnTrue() {
        request.putSampleIfAbsent(getSample("id1", Recipe.RNA_SEQ_POLY_A));
        request.putSampleIfAbsent(getSample("id2", Recipe.RNA_SEQ_POLY_A));

        assertThat(recipeValidator.isValid(request), is(true));
    }

    @Test
    public void whenRecipesContainTwoDifferentValues_shouldReturnFalse() {
        request.putSampleIfAbsent(getSample("id1", Recipe.RNA_SEQ_POLY_A));
        request.putSampleIfAbsent(getSample("id2", Recipe.AMPLI_SEQ));
        assertThat(recipeValidator.isValid(request), is(false));
    }

    @Test
    public void whenRecipesContainMultipleSameValueAndOneDifferent_shouldReturnFalse() {
        request.putSampleIfAbsent(getSample("id1", Recipe.RNA_SEQ_POLY_A));
        request.putSampleIfAbsent(getSample("id2", Recipe.RNA_SEQ_POLY_A));
        request.putSampleIfAbsent(getSample("id3", Recipe.ARCHER_FUSION_PLEX));
        request.putSampleIfAbsent(getSample("id4", Recipe.RNA_SEQ_POLY_A));

        assertThat(recipeValidator.isValid(request), is(false));
    }

    private Sample getSample(String id, Recipe recipe) {
        Sample sample = new Sample(id);
        sample.setRecipe(recipe);
        return sample;
    }
}