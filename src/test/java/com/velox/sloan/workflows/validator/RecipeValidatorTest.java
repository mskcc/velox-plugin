package com.velox.sloan.workflows.validator;

import com.velox.api.datarecord.DataRecord;
import com.velox.api.user.User;
import com.velox.sloan.workflows.notificator.Notificator;
import org.junit.Before;
import org.junit.Test;
import org.mskcc.domain.Recipe;
import org.mskcc.domain.Request;
import org.mskcc.domain.Sample;

import java.util.Collections;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.mock;

public class RecipeValidatorTest {
    private Request request;
    private Notificator notificator = mock(Notificator.class);
    private User user = mock(User.class);
    private Map<String, DataRecord> sampleIdToRecords = Collections.emptyMap();

    @Before
    public void setUp() {
        request = new Request("someId");
    }

    @Test
    public void whenRecipesListIsEmpty_shouldReturnTrue() {
        RecipeValidator recipeValidator = new RecipeValidator(notificator, user, sampleIdToRecords);

        assertThat(recipeValidator.isValid(request), is(true));
    }

    @Test
    public void whenRecipesContainOneValue_shouldReturnTrue() {
        RecipeValidator recipeValidator = new RecipeValidator(notificator, user, sampleIdToRecords);

        Sample sample = getSample("id1", Recipe.RNA_SEQ_POLY_A);
        request.putSampleIfAbsent(sample);
        assertThat(recipeValidator.isValid(request), is(true));
    }

    private Sample getSample(String id, Recipe recipe) {
        Sample sample = new Sample(id);
        sample.setRecipe(recipe);
        return sample;
    }

    @Test
    public void whenRecipesContainTwoSameValues_shouldReturnTrue() {
        RecipeValidator recipeValidator = new RecipeValidator(notificator, user, sampleIdToRecords);

        request.putSampleIfAbsent(getSample("id1", Recipe.RNA_SEQ_POLY_A));
        request.putSampleIfAbsent(getSample("id2", Recipe.RNA_SEQ_POLY_A));

        assertThat(recipeValidator.isValid(request), is(true));
    }

    @Test
    public void whenRecipesContainTwoDifferentValues_shouldReturnFalse() {
        RecipeValidator recipeValidator = new RecipeValidator(notificator, user, sampleIdToRecords);

        request.putSampleIfAbsent(getSample("id1", Recipe.RNA_SEQ_POLY_A));
        request.putSampleIfAbsent(getSample("id2", Recipe.AMPLI_SEQ));
        assertThat(recipeValidator.isValid(request), is(false));
    }

    @Test
    public void whenRecipesContainMultipleSameValueAndOneDifferent_shouldReturnFalse() {
        RecipeValidator recipeValidator = new RecipeValidator(notificator, user, sampleIdToRecords);

        request.putSampleIfAbsent(getSample("id1", Recipe.RNA_SEQ_POLY_A));
        request.putSampleIfAbsent(getSample("id2", Recipe.RNA_SEQ_POLY_A));
        request.putSampleIfAbsent(getSample("id3", Recipe.ARCHER_FUSION_PLEX));
        request.putSampleIfAbsent(getSample("id4", Recipe.RNA_SEQ_POLY_A));

        assertThat(recipeValidator.isValid(request), is(false));
    }
}